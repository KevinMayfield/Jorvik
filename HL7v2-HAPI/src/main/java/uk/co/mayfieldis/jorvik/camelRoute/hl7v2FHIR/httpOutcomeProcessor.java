package uk.co.mayfieldis.jorvik.camelRoute.hl7v2FHIR;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class httpOutcomeProcessor implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.camelRoute.hl7v2FHIR.httpOutcomeProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		// 
		ByteArrayInputStream xmlContentBytes = null;
		int n = 0;
		byte[] bytes = null;
		String s = null;
		
		if (exchange.getOut() != null && exchange.getOut().getBody() != null)
		{
			xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getOut().getBody(byte[].class));
			if (xmlContentBytes != null )
			{
				n = xmlContentBytes.available();
				bytes = new byte[n];
				xmlContentBytes.read(bytes, 0, n);
				s = new String(bytes, StandardCharsets.UTF_8); // Or any encoding.
				if (s != null && !s.isEmpty())
				{
					log.warn("Output Stream = "+s);
				}
			}
		}
		if (exchange.getIn() != null && exchange.getIn().getBody() != null)
		{
			xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
			if (xmlContentBytes != null )
			{
				n = xmlContentBytes.available();
				bytes = new byte[n];
				xmlContentBytes.read(bytes, 0, n);
				s = new String(bytes, StandardCharsets.UTF_8); // Or any encoding.
				if (s != null && !s.isEmpty())
				{
					log.warn("Input Stream = "+s);
				}	
			}
		}
	}

}
