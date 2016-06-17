package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Period;

import org.hl7.fhir.instance.model.EpisodeOfCare;
import org.hl7.fhir.instance.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;

public class ADTA01A04A08toEpisodeOfCare implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEpisodeOfCare.class);
	
	Terser terser = null;
	
	public NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
	public Environment env;
	
	private String terserGet(String query)
	{
		String result = "";
		try
		{
			result = terser.get(query);
			//log.info(query+" = "+result);
		}
		catch(HL7Exception hl7ex)
		{
			// Could add some extra code here
			
			log.debug("#1 "+hl7ex.getMessage());
		}
		catch(Exception ex)
		{
			// Exception thrown on no data
			log.debug("#2 "+ex.getMessage());
		}
		
		return result;
	}
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Message message = exchange.getIn().getBody(Message.class);
		
		EpisodeOfCare episode = new EpisodeOfCare();
		
		// Use Terser as code is more readable
		terser = new Terser(message);
		
		Integer maxRepitions = 5;
		
		try
		{
			// Identifiers PID.PatientIdentifierList()
			for (int f=0;f<maxRepitions;f++)
			{
				String code =null;
				String value =null;
				if (f==0)
				{
					code =terserGet("/.PID-3-4");
					value =terserGet("/.PID-3-1");		
				}
				else
				{
					code =terserGet("/.PID-3("+f+")-4");
					value =terserGet("/.PID-3("+f+")-1");
				}
				if (code != null && !code.isEmpty())
				{
					log.debug("Code = "+code);
					switch (code)
					{
						case "PAS":
							if (exchange.getIn().getHeader("FHIRPatient") !=null && exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							break;
						case "NHS":
							if (exchange.getIn().getHeader("FHIRPatient") !=null && exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							break;
						default:
							exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							break;
					}
					log.debug("FHIRPatient  = "+exchange.getIn().getHeader("FHIRPatient").toString());
				}
			}
			// Names PID.PatientName
			log.debug("Activity ID");
			if (terserGet("/.PV1-50-1") != null && !terserGet("/.PV1-50-1").isEmpty())
			{
				episode.addIdentifier()
					.setSystem(env.getProperty("ORG.TrustEpisodeOfCare"))
					.setValue(terserGet("/.PV1-50-1"));
			}
			// StartDate
			episode.setStatus(EpisodeOfCareStatus.ACTIVE);
			
			
			if (terserGet("/.PV1-2") != null)
			{
				exchange.getIn().setHeader("FHIREpisodeType", terserGet("/.PV1-2"));
				switch (terserGet("/.PV1-2"))
				{
					case "O" : 
					case "E" :	
					case "I" : 
						break;
					case "P" :
						episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.PLANNED);
						break;
					case "W" :
						episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.WAITLIST);
						break;
					
				}
			}
			if (terserGet("/.MSH-9-2") != null)
			{
				if (terserGet("/.MSH-9-2").equals("A03"))
				{
					episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.FINISHED);
				}
				if (terserGet("/.MSH-9-2") != null && terserGet("/.MSH-9-2").equals("A11"))
				{
					episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.CANCELLED);
				}
			}

			
			Period period = new Period();
			if (terserGet("/.PV1-44-1") != null && !terserGet("/.PV1-44-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.PV1-44-1"));
	        		period.setStart(date);
	        		
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			if (terserGet("/.PV1-45-1") != null && !terserGet("/.PV1-45-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.PV1-45-1"));
	        		period.setEnd(date);
	        		
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			// Last updated
			if (terserGet("/.EVN-2-1") != null && !terserGet("/.EVN-2-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.EVN-2-1"));
	        		episode.getMeta().setLastUpdated(date);
	        		
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			episode.setPeriod(period);
			log.debug("Pre-Specialties");
			if (terserGet("/.PV1-10-1") != null && !terserGet("/.PV1-10-1").isEmpty())
			{
				episode.addType()
					.addCoding()
						.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES)
						.setCode(terserGet("/.PV1-10-1"));
			}
			
			log.debug("Get Reference Material");
						
			if (terserGet("/.PV1-50-1") != null && !terserGet("/.PV1-50-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIREpisode", terserGet("/.PV1-50-1"));
			}
			if (terserGet("/.PV1-3-1") != null && !terserGet("/.PV1-3-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIROrganisationCode", terserGet("/.PV1-3-1"));
			}
			
			if (terserGet("/.PV1-7-1") != null && !terserGet("/.PV1-7-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIRPractitioner", terserGet("/.PV1-7-1"));
			}
			
			
			switch (terserGet("/.MSH-9-2"))
			{
				case "A01":
				case "A08":
				case "A03":
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"POST");
					break;
				default:
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"PUT");	
			}
		}
		catch (Exception ex)
		{
			log.error("#3 "+ exchange.getExchangeId() + " "  + ex.getMessage() 
					+" Properties: " + exchange.getProperties().toString()
					+" Headers: " + exchange.getIn().getHeaders().toString() 
					+ " Message:" + exchange.getIn().getBody().toString());
		}
		exchange.getIn().setHeader("FHIRResource", "EpisodeOfCare");
		String Response = ResourceSerialiser.serialise(episode, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");

		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
