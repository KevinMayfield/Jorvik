package uk.co.mayfieldis.jorvik.core.camel;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.context.FhirContext;

public class EnrichEncounterwithPractitioner implements AggregationStrategy {

//	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.EnrichEncounterwithPractitioner.class);
	public EnrichEncounterwithPractitioner(FhirContext ctx)
	{
		this.ctx = ctx;
		
	}
	private  FhirContext ctx;
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) {
		
		Bundle bundle = null;
		
		Encounter encounter = null;
		
		try
		{
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");

			if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString().equals("200") && enrichment.getIn().getBody() != null)
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
						if (bundle.getEntry().size()>0)
						{
							encounter = parser.parseResource(Encounter.class, readerNew);
							Reference ref = new Reference();
							Practitioner practitioner = (Practitioner) bundle.getEntry().get(0).getResource(); 
							ref.setReference("Practitioner/"+practitioner.getIdElement().getIdPart());
							encounter.addParticipant().setIndividual(ref);
							String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounter);
							//String Response = ResourceSerialiser.serialise(encounter, ParserType.XML);
							exchange.getIn().setBody(Response);
						}
					}
					catch(Exception ex)
					{
						
						ex.printStackTrace();
						throw ex;
					}
					
				
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
