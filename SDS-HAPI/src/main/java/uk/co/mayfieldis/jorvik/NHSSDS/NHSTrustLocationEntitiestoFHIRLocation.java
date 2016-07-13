package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Location;

import ca.uhn.fhir.context.FhirContext;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;



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
		
		if (entity.managingOrganization != null && !entity.managingOrganization.isEmpty())
		{
			exchange.getIn().setHeader("FHIROrganisationCode",entity.managingOrganization);
		}
		
		if (entity.PartOf != null && !entity.PartOf.isEmpty())
		{
			exchange.getIn().setHeader("FHIRLocation",entity.PartOf);
		}
		
		String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(location);
		//String Response = ResourceSerialiser.serialise(location, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader("FHIRResource","/Location");
		exchange.getIn().setHeader("FHIRQuery","identifier="+location.getIdentifier().get(0).getSystem()+"|"+location.getIdentifier().get(0).getValue());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
