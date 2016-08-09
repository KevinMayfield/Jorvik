package uk.co.mayfieldis.jorvik.NHSSDS;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerPractitionerRoleComponent;
import org.hl7.fhir.dstu3.model.valuesets.PractitionerRole;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;

//8-Aug-2016 KGM Replaced lookup code with FHIR Transactions

public class NHSConsultantEntitiestoFHIRPractitioner implements Processor {

	public NHSConsultantEntitiestoFHIRPractitioner(FhirContext ctx)
	{
		this.ctx = ctx;
	}
	private FhirContext ctx;
	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSConsultantEntities entity = (NHSConsultantEntities) exchange.getIn().getBody(NHSConsultantEntities.class);
		
		String Id = entity.PractitionerCode; 
		
		Organization parentOrg = new Organization();
		parentOrg.setId(IdDt.newRandomUuid());
		
		parentOrg.addIdentifier()
			.setValue(entity.LocationOrganisationCode)
			.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
		
		
		if ( Id.startsWith("C") && Id.length()>6)
		{
			Practitioner gp = new Practitioner();
			//gp.setId(entity.OrganisationCode);
			
			gp.addIdentifier()
				.setValue(entity.PractitionerCode)
				.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
			
			
			HumanName name = new HumanName();
			
			if (!entity.Surname.isEmpty()) 
			{
				name.addFamily(entity.Surname);
			}
			if (!entity.Initials.isEmpty()) 
			{
				name.addGiven(entity.Initials);
			}
			gp.addName(name);
			
			PractitionerPractitionerRoleComponent practitionerRole = new PractitionerPractitionerRoleComponent();
			
			CodeableConcept role= new CodeableConcept();
			role.addCoding()
					.setCode(PractitionerRole.DOCTOR.toString())
					.setSystem("http://hl7.org/fhir/practitioner-role");
			
			practitionerRole.setRole(role);
									
			CodeableConcept pracspecialty= new CodeableConcept();
			pracspecialty.addCoding()
				.setCode(entity.SpecialityFunctionCode)
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
			practitionerRole
				.addSpecialty(pracspecialty);
			practitionerRole.setOrganization(new Reference(parentOrg.getId()));
			
			gp.addPractitionerRole(practitionerRole);

			Bundle bundle = new Bundle();
			
			bundle.setType(BundleType.TRANSACTION);
			
			bundle.addEntry()
			   .setFullUrl(parentOrg.getId())
			   .setResource(parentOrg)
			   .getRequest()
			      .setUrl("Organization")
			      .setIfNoneExist("Organization?identifier="+parentOrg.getIdentifier().get(0).getSystem()+"|"+parentOrg.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
		
			bundle.addEntry()
			   .setResource(gp)
			   .getRequest()
			      .setUrl("Practitioner?identifier="+gp.getIdentifier().get(0).getSystem()+"|"+gp.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.PUT);
			
			// XML as Ensemble doesn't like JSON
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
			
			exchange.getIn().setHeader("FHIRResource","/");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			exchange.getIn().setBody(Response);
		}
	}

}
