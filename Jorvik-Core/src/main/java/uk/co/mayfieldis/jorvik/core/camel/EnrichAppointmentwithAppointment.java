package uk.co.mayfieldis.jorvik.core.camel;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.parser.IParser;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;

public class EnrichAppointmentwithAppointment implements AggregationStrategy {

	//private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.EnrichAppointmentwithAppointment.class);
	public EnrichAppointmentwithAppointment(FhirContext ctx)
	{
		this.ctx = ctx;
		
	}
	private FhirContext ctx;
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) {
		
		Bundle bundle = null;
		
		Appointment appointment = null;
		
		try
		{
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");

			if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString().equals("200"))
			{
				
				//ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
				Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class)));
				
				if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
				{
					//JsonParser parser = new JsonParser();
					IParser parser = ctx.newJsonParser();
					
					try
					{
						bundle = parser.parseResource(Bundle.class, reader);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw ex;
					}
				}
				else
				{
					// XmlParser parser = new XmlParser();
					IParser parser = ctx.newXmlParser();
					try
					{
						bundle = parser.parseResource(Bundle.class, reader);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw ex;
					}
				}
				//ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				Reader readerNew = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
				//XmlParser parser = new XmlParser();
				IParser parser = ctx.newXmlParser();
				try
				{
					appointment = parser.parseResource(Appointment.class, readerNew);
					if (bundle!=null && bundle.getEntry().size()>0)
					{
						Appointment hapiAppointment = (Appointment) bundle.getEntry().get(0).getResource();  
						appointment.setId(hapiAppointment.getId());
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"Appointment/"+hapiAppointment.getIdElement().getIdPart());
						// Have altered resource so process it.
						String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(appointment);
						//String Response = ResourceSerialiser.serialise(appointment, ParserType.XML);
						exchange.getIn().setBody(Response);
					}
					else
					{
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"Appointment");
						
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					throw ex;
					/*
					log.error("#12 XML Parse failed 2"+ exchange.getExchangeId() + " "  + ex.getMessage() 
					+" Properties: " + exchange.getProperties().toString()
					+" Headers: " + exchange.getIn().getHeaders().toString() 
					+ " Message:" + exchange.getIn().getBody().toString());
					*/
				}
				
				exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		
		return exchange;
	}

}

