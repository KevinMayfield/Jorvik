package uk.co.mayfieldis.jorvik.core.camel;

import java.io.ByteArrayInputStream;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.EpisodeOfCare;
import org.hl7.fhir.instance.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent;
import org.hl7.fhir.instance.model.Period;


public class EnrichEpisodewithEpisode implements AggregationStrategy {

//	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.core.EnrichEncounterwithEncounter.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) {
		
		Bundle bundle = null;
		
		EpisodeOfCare episode = null;
		
		try
		{
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");

			if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString().equals("200"))
			{
				
				ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
				
				
				if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
				{
					JsonParser composer = new JsonParser();
					try
					{
						bundle = (Bundle) composer.parse(xmlContentBytes);
					}
					catch(Exception ex)
					{
	//					log.error("#9 JSON Parse failed "+ex.getMessage());
					}
				}
				else
				{
					XmlParser composer = new XmlParser();
					try
					{
						bundle = (Bundle) composer.parse(xmlContentBytes);
					}
					catch(Exception ex)
					{
		//				log.error("#10 XML Parse failed "+ex.getMessage());
					}
				}
				ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				
				XmlParser composer = new XmlParser();
				try
				{
					episode = (EpisodeOfCare) composer.parse(xmlNewContentBytes);
					if (bundle.getEntry().size()>0)
					{
						EpisodeOfCare hapiEpisode = (EpisodeOfCare) bundle.getEntry().get(0).getResource();  
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
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"EpisodeOfCare/"+hapiEpisode.getId());
						// Have altered resource so process it.
						String Response = ResourceSerialiser.serialise(episode, ParserType.XML);
						exchange.getIn().setBody(Response);
					}
					else
					{
						exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
						exchange.getIn().setHeader(Exchange.HTTP_PATH,"EpisodeOfCare");
						
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

