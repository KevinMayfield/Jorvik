package uk.co.mayfieldis.jorvik.core.camel;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;

import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;


public class EnrichResourcewithOrganisation implements AggregationStrategy  {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.camel.EnrichResourcewithOrganisation.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) 
	{
		try
		{
			Organization parentOrganisation = null;
			//
			log.debug("Response Code = " +enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString());
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
				  
			}
			
			/*
			 * 
			 *  PASTE
			 * 
			 */
			log.debug("Adding Org to Entity");
			String Id = exchange.getIn().getHeader("NHSEntityId").toString();
			
			log.debug("NHSEntity = "+Id);
			if ( (Id.startsWith("G") || Id.startsWith("C")) && Id.length()>6)
			{
				
				if (parentOrganisation !=null)
				{
					
					ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
					
					XmlParser composer = new XmlParser();
					try
					{
						// Add in the parent organisation code
						Practitioner gp = (Practitioner) composer.parse(xmlNewContentBytes);
						
						PractitionerPractitionerRoleComponent practitionerRole = gp.getPractitionerRole().get(0);
					
	
						Reference organisation = new Reference();
						organisation.setReference("Organization/"+parentOrganisation.getId());
						practitionerRole.setManagingOrganization(organisation);
						Extension parentOrg= new Extension();
						parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
						CodeableConcept parentCode = new CodeableConcept();
						parentCode.addCoding()
							.setCode(exchange.getIn().getHeader("FHIROrganisationCode").toString())
							.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
						
						parentOrg.setValue(parentCode);
						practitionerRole.addExtension(parentOrg);
						
						String Response = ResourceSerialiser.serialise(gp, ParserType.XML);
						exchange.getIn().setBody(Response);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw ex;
					}
					
				}
			}
			else
			{
				
				if (parentOrganisation !=null)
				{
					ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
					
					XmlParser composer = new XmlParser();
					try
					{
						// Add in the parent organisation code
						log.debug("Adding organisation");
						Organization organisation = (Organization) composer.parse(xmlNewContentBytes);
					
						Reference ccg = new Reference();
						ccg.setReference("/Organization/"+parentOrganisation.getId());
						organisation.setPartOf(ccg);
						log.debug("Adding organisation Ref= "+ccg.getReference());
						Extension parentOrg= new Extension();
						parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
						CodeableConcept parentCode = new CodeableConcept();
						parentCode.addCoding()
							.setCode(exchange.getIn().getHeader("FHIROrganisationCode").toString())
							.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
					
						parentOrg.setValue(parentCode);
						organisation.addExtension(parentOrg);
						
						String Response = ResourceSerialiser.serialise(organisation, ParserType.XML);
						exchange.getIn().setBody(Response);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw ex;
					}
				}
				else
				{
					log.debug("No parent org found");
				}
			}
			/*
			 *  ENDP
			 */
		
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
			log.error(ex.getMessage());
		}
		log.debug("Finish Enrich with Org");
		return exchange;
	}

	
}


