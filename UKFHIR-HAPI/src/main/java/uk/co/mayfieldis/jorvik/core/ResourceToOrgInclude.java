package uk.co.mayfieldis.jorvik.core;

import java.io.ByteArrayInputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceToOrgInclude implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.ResourceToOrgInclude.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
		Practitioner practitioner = null;
		Organization organisation = null;
		
		
		if (exchange.getIn().getHeader(Exchange.HTTP_PATH).toString().contains("Practitioner"))
		{
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"_include=Practitioner:organization&_format=xml");
		}
		else if (exchange.getIn().getHeader(Exchange.HTTP_PATH).toString().contains("Organization"))
		{
		
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"_include=_include=Organization:partof&_format=xml");
		}
		
		if (exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString().equals("POST"))
		{
			// Get the ID's of the posted resource
			XmlParser composer = new XmlParser();
			try
			{
				if (exchange.getIn().getHeader(Exchange.HTTP_PATH).toString().contains("Practitioner"))
				{
					practitioner = (Practitioner) composer.parse(xmlContentBytes);
					if (practitioner != null)
					{
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"Practitioner/"+practitioner.getId());
						exchange.getIn().setHeader(Exchange.HTTP_QUERY,"_include=Practitioner:organization&_format=xml");
					}
				}
				else if (exchange.getIn().getHeader(Exchange.HTTP_PATH).toString().contains("Organization"))
				{
					organisation = (Organization) composer.parse(xmlContentBytes);
					if (organisation != null)
					{
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"Organization/"+organisation.getId());
						exchange.getIn().setHeader(Exchange.HTTP_QUERY,"_include=Organization:partof&_format=xml");
					}
				}
			}
			catch(Exception ex)
			{
				log.error("#1 XML Parse failed "+ex.getMessage());
			}
		}
		exchange.getIn().setBody("");
		// Pop old method into stack for later use
		exchange.getIn().setHeader("FHIRMethod", exchange.getIn().getHeader(Exchange.HTTP_METHOD));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
		
		
	}

}
