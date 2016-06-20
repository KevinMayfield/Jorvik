package uk.co.mayfieldis.jorvik.hl7v2.processor;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Location;
import org.hl7.fhir.instance.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import uk.co.mayfieldis.jorvik.core.camel.ResourceSerialiser;

public class FHIRResourcetoLocation implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.FHIRResourcetoLocation.class);
	
		
	public Environment env;
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		try
		{
			log.info("Location called "+exchange.getIn().getHeader("FHIRLocation").toString());
			Location location = new Location();
			
			location.addIdentifier()
					.setSystem(env.getProperty("ORG.uriNHSOrgLocation"))
					.setValue(exchange.getIn().getHeader("FHIRLocation").toString().replace(' ', '-'));
				
			if (exchange.getIn().getHeader("FHIROrganisationRef") != null && !exchange.getIn().getHeader("FHIROrganisationRef").toString().isEmpty())
			{
				Reference ref = new Reference();
				ref.setReference(exchange.getIn().getHeader("FHIROrganisationRef").toString());
				location.setManagingOrganization(ref);
			}
			
			String Response = ResourceSerialiser.serialise(location, ParserType.XML);
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
			// This will only post the resource if it doesn't exist. The resource isn't complete but we need a stug for referential integrity
			exchange.getIn().setHeader("If-None-Exist","identifier="+env.getProperty("ORG.uriNHSOrgLocation")+"|"+exchange.getIn().getHeader("FHIRLocation").toString().replace(' ','-'));
			exchange.getIn().setHeader(Exchange.HTTP_PATH,"Location");
			exchange.getIn().setBody(Response);
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			log.info("Location Body "+Response);
		}	
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		
	}

}
