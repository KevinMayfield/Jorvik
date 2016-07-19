package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class EncountertoEpisodeOfCare implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.EncountertoEpisodeOfCare.class);
	
	public EncountertoEpisodeOfCare(FhirContext ctx, Environment env)
	{
		this.ctx = ctx;
		this.env = env;
		
	}
	
	private Environment env;
	
	public FhirContext ctx;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (exchange.getIn().getHeader("FHIRResource")=="Encounter")
		{
			//ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
			Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
			
			Encounter encounter = null;
			if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).equals("application/json"))
			{
				//JsonParser parser = new JsonParser();
				IParser parser = ctx.newJsonParser();
				
				encounter = parser.parseResource(Encounter.class,reader);
			}
			else
			{
				// XmlParser parser = new XmlParser();
				IParser parser = ctx.newXmlParser();
				encounter = parser.parseResource(Encounter.class,reader);
			}
			
			EpisodeOfCare episode = new EpisodeOfCare();
			
			
			try
			{
				episode.addIdentifier()
					.setSystem(env.getProperty("ORG.TrustEpisodeOfCare"))
					.setValue(exchange.getIn().getHeader("FHIREpisode").toString());
				
				episode.setPatient(encounter.getPatient());
				episode.setPeriod(episode.getPeriod());
				log.info("Encounter State = "+encounter.getStatus().toString() + " Episode Id = "+exchange.getIn().getHeader("FHIREpisode").toString());
				switch (encounter.getStatus().toString())
				{
					case "planned":
						episode.setStatus(EpisodeOfCareStatus.PLANNED);
						break;
					case "arrived":
						episode.setStatus(EpisodeOfCareStatus.ACTIVE);
						break;
					case "in-progress":
						episode.setStatus(EpisodeOfCareStatus.ACTIVE);
						break;
					case "onleave":
						episode.setStatus(EpisodeOfCareStatus.ACTIVE);
						break;
					case "finished":
						// May not be true
						episode.setStatus(EpisodeOfCareStatus.FINISHED);
						break;
					case "cancelled":
						// May not be true
						episode.setStatus(EpisodeOfCareStatus.CANCELLED);
						break;
				}
				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				throw ex;
			}
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(episode);
			log.info("Episode ("+exchange.getIn().getHeader("FHIREpisode").toString()+") to add if not exist is = "+ Response);
			//String Response = ResourceSerialiser.serialise(episode, ParserType.XML);
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
			// This will only post the resource if it doesn't exist. The resource isn't complete but we need a stug for referential integrity
			exchange.getIn().setHeader("If-None-Exist","identifier="+env.getProperty("ORG.TrustEpisodeOfCare")+"|"+exchange.getIn().getHeader("FHIREpisode"));
			exchange.getIn().setHeader(Exchange.HTTP_PATH,"EpisodeOfCare");
			exchange.getIn().setBody(Response);
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		}
	}

}
