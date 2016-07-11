package uk.co.mayfieldis.jorvik.core.camel;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent;
import org.hl7.fhir.dstu3.model.Period;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;




public class EnrichEpisodewithEpisode implements AggregationStrategy {

//	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.EnrichEncounterwithEncounter.class);
	public FhirContext ctx;
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) {
		
		Bundle bundle = null;
		
		EpisodeOfCare episode = null;
		
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
						bundle = parser.parseBundle(reader);
					}
					catch(Exception ex)
					{
	//					log.error("#9 JSON Parse failed "+ex.getMessage());
					}
				}
				else
				{
					// XmlParser parser = new XmlParser();
					IParser parser = ctx.newXmlParser();
					try
					{
						bundle = parser.parseBundle(reader);
					}
					catch(Exception ex)
					{
		//				log.error("#10 XML Parse failed "+ex.getMessage());
					}
				}
				//ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				Reader readerNew = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
				//XmlParser parser = new XmlParser();
				IParser parser = ctx.newXmlParser();
				try
				{
					episode = parser.parseResource(EpisodeOfCare.class, readerNew);
					if (bundle.getEntries().size()>0)
					{
						EpisodeOfCare hapiEpisode = (EpisodeOfCare) bundle.getEntries().get(0).getResource();  
						episode.setId(hapiEpisode.getId());
						// Copy status history over
						for (int stno=0; stno<hapiEpisode.getStatusHistory().size();stno++)
						{
							episode.addStatusHistory(hapiEpisode.getStatusHistory().get(stno));
						}
						if (!hapiEpisode.getStatus().equals(episode.getStatus()))
						{
							EpisodeOfCareStatusHistoryComponent statusHistory = new EpisodeOfCareStatusHistoryComponent();
							statusHistory.setStatus(hapiEpisode.getStatus());
							Period period = new Period();
							period.setStart(hapiEpisode.getMeta().getLastUpdated());
							period.setEnd(episode.getMeta().getLastUpdated());
							statusHistory.setPeriod(period);
							episode.addStatusHistory(statusHistory);
						}
						if (hapiEpisode.getPeriod() != null && episode.getPeriod() != null)
						{
							if (hapiEpisode.getPeriod().getStart() != null)
							{
								// Restore original start date
								episode.getPeriod().setStart(hapiEpisode.getPeriod().getStart());
							}
							if ((episode.getStatus().equals(EpisodeOfCare.EpisodeOfCareStatus.FINISHED) || episode.getStatus().equals(EpisodeOfCare.EpisodeOfCareStatus.CANCELLED)))
							{
								// do nothing
							
							}
							else
							{
								// Remove end date as episode not finished
								episode.getPeriod().setEnd(null);
							}
							
						}
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"EpisodeOfCare/"+hapiEpisode.getId());
						// Have altered resource so process it.
						String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(episode);
						//String Response = ResourceSerialiser.serialise(episode, ParserType.XML);
						exchange.getIn().setBody(Response);
					}
					else
					{
						if (episode.getPeriod() != null)
						{
							if ((episode.getStatus().equals(EpisodeOfCare.EpisodeOfCareStatus.FINISHED) || episode.getStatus().equals(EpisodeOfCare.EpisodeOfCareStatus.CANCELLED)))
							{
								// Remove end date as episode not finished
								episode.getPeriod().setEnd(null);
							}
						}
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"EpisodeOfCare");
						//String Response = ResourceSerialiser.serialise(episode, ParserType.XML);
						String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(episode);
						exchange.getIn().setBody(Response);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					throw ex;
				}
				
				exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			//throw ex;
		}
		
		return exchange;
	}

}

