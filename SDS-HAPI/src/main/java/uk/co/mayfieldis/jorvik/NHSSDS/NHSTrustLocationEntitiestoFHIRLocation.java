package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Location;

import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;


public class NHSTrustLocationEntitiestoFHIRLocation implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSTrustLocationEntities entity = exchange.getIn().getBody(NHSTrustLocationEntities.class);
		
		Location location = new Location();
		
		location.addIdentifier()
			.setSystem(NHSTrustFHIRCodeSystems.uriCHFTLocation)
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
			exchange.getIn().setHeader("ParentOrganisationCode",entity.managingOrganization);
		}
		
		if (entity.PartOf != null && !entity.PartOf.isEmpty())
		{
			exchange.getIn().setHeader("FHIRLocation",entity.PartOf);
		}
		
		String Response = ResourceSerialiser.serialise(location, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader("FHIRResource","/Location");
		exchange.getIn().setHeader("FHIRQuery","identifier="+location.getIdentifier().get(0).getSystem()+"|"+location.getIdentifier().get(0).getValue());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
