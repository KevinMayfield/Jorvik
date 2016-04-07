package uk.co.mayfieldis.jorvik.core;

import java.io.ByteArrayInputStream;


import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;


import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;

public class EnrichConsultantwithOrganisation implements AggregationStrategy  {

//	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.EnrichConsultantwithOrganisation.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) 
	{
		
		
		
		Organization parentOrganisation = null;
		Practitioner gp = null;
		//
		if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString().equals("200"))
		{
			ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
			
			
			if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
			{
				JsonParser composer = new JsonParser();
				try
				{
					Bundle bundle = (Bundle) composer.parse(xmlContentBytes);
					if (bundle.getEntry().size()>0)
					{
						parentOrganisation = (Organization) bundle.getEntry().get(0).getResource();
					}
				}
				catch(Exception ex)
				{
					
				}
			}
			else
			{
				XmlParser composer = new XmlParser();
				try
				{
					Bundle bundle = (Bundle) composer.parse(xmlContentBytes);
					if (bundle.getEntry().size()>0)
					{
						parentOrganisation = (Organization) bundle.getEntry().get(0).getResource();
					}
				}
				catch(Exception ex)
				{
					
				}
			}
			
			if (parentOrganisation !=null)
			{
				ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				
				XmlParser composer = new XmlParser();
				try
				{
					// Add in the parent organisation code
					gp = (Practitioner) composer.parse(xmlNewContentBytes);
					
					PractitionerPractitionerRoleComponent practitionerRole = gp.getPractitionerRole().get(0);
									
					Reference organisation = new Reference();
					organisation.setReference("Organization/"+parentOrganisation.getId());
					practitionerRole.setManagingOrganization(organisation);
					Extension parentOrg= new Extension();
					parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
					CodeableConcept parentCode = new CodeableConcept();
					parentCode.addCoding()
						.setCode(exchange.getIn().getHeader("ParentOrganisationCode").toString())
						.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
					
					parentOrg.setValue(parentCode);
					practitionerRole.addExtension(parentOrg);
					
					String Response = ResourceSerialiser.serialise(gp, ParserType.XML);
					exchange.getIn().setBody(Response);
				}
				catch(Exception ex)
				{
	//				log.error("#12 XML Parse failed 2"+ exchange.getExchangeId() + " "  + ex.getMessage() 
	//					+" Properties: " + exchange.getProperties().toString()
	//					+" Headers: " + exchange.getIn().getHeaders().toString() 
	//					+ " Message:" + exchange.getIn().getBody().toString());
				}
			}
		}
		
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		return exchange;
	}
}


