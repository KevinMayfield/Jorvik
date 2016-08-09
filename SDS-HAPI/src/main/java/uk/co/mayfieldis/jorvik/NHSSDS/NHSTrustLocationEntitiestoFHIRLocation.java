package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;

//8-Aug-2016 KGM Replaced lookup code with FHIR Transactions

public class NHSTrustLocationEntitiestoFHIRLocation implements Processor {

	public NHSTrustLocationEntitiestoFHIRLocation(FhirContext ctx, NHSTrustFHIRCodeSystems TrustFHIRSystems)
	{
		this.ctx = ctx;
		this.TrustFHIRSystems = TrustFHIRSystems;
	}
	private NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
	private FhirContext ctx;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSTrustLocationEntities entity = exchange.getIn().getBody(NHSTrustLocationEntities.class);
		
		Location location = new Location();
		
		location.addIdentifier()
			.setSystem(TrustFHIRSystems.geturiNHSOrgLocation())
			.setValue(entity.LocalCode);
		
		if (entity.Description != null && !entity.Description.isEmpty())
		{
			location.setName(entity.Description);
		}
		if (entity.Type != null && !entity.Type.isEmpty())
		{
			CodeableConcept type = new CodeableConcept();
			type.addCoding()
				.setCode(entity.Type)
				.setSystem("http://hl7.org/fhir/ValueSet/v3-ServiceDeliveryLocationRoleType");
			location.setType(type);
		}
		
		Bundle bundle = new Bundle();
		
		bundle.setType(BundleType.TRANSACTION);
				
		if (entity.managingOrganization != null && !entity.managingOrganization.isEmpty())
		{
			// exchange.getIn().setHeader("FHIROrganisationCode",entity.managingOrganization);
			
			// Build basic parent resource
			Organization parentOrg = new Organization();
			parentOrg.setId(IdDt.newRandomUuid());
			parentOrg.addIdentifier()
				.setValue(entity.managingOrganization)
				.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
			
			// Create reference in main resource
			location.setManagingOrganization(new Reference(parentOrg.getId()));
			
			bundle.addEntry()
			   .setFullUrl(parentOrg.getId())
			   .setResource(parentOrg)
			   .getRequest()
			      .setUrl("Organization")
			      .setIfNoneExist("Organization?identifier="+parentOrg.getIdentifier().get(0).getSystem()+"|"+parentOrg.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
		}
		
		
		if (entity.PartOf != null && !entity.PartOf.isEmpty())
		{
			exchange.getIn().setHeader("FHIRLocation",entity.PartOf);
			
			Location parentLoc = new Location();
			parentLoc.setId(IdDt.newRandomUuid());
			parentLoc.addIdentifier()
				.setValue(entity.PartOf)
				.setSystem(TrustFHIRSystems.geturiNHSOrgLocation());
			
			// Create reference in main resource
			location.setPartOf(new Reference(parentLoc.getId()));
			
			bundle.addEntry()
			   .setFullUrl(parentLoc.getId())
			   .setResource(parentLoc)
			   .getRequest()
			      .setUrl("Location")
			      .setIfNoneExist("Location?identifier="+parentLoc.getIdentifier().get(0).getSystem()+"|"+parentLoc.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
		}
		
		bundle.addEntry()
		   .setResource(location)
		   .getRequest()
		      .setUrl("Location?identifier="+location.getIdentifier().get(0).getSystem()+"|"+location.getIdentifier().get(0).getValue())
		      .setMethod(HTTPVerb.PUT);
		
		// Change me
		String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
		
		exchange.getIn().setHeader("FHIRResource","/");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setBody(Response);
		
	}

}
