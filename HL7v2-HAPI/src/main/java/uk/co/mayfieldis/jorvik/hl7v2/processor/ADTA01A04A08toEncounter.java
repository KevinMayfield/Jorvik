package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Period;

import org.hl7.fhir.instance.model.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.camel.ResourceSerialiser;

public class ADTA01A04A08toEncounter implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEncounter.class);
	
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
		
		Encounter encounter = new Encounter();
		
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
					log.info("PID "+code+" "+value);
					switch (code)
					{
						case "PAS":
							if (exchange.getIn().getHeader("FHIRPatient") ==null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							break;
						case "NHS":
							if (exchange.getIn().getHeader("FHIRPatient") ==null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							break;
						default:
							exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							break;
					}
					
				}
			}
			log.info("FHIRPatient  = "+exchange.getIn().getHeader("FHIRPatient").toString());
			// Names PID.PatientName
			log.info("Activity ID");
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				encounter.addIdentifier()
					.setSystem(TrustFHIRSystems.geturiNHSOrgActivityId())
					.setValue(terserGet("/.PV1-19-1"));
			}
			// StartDate
			encounter.setStatus(Encounter.EncounterState.ARRIVED);
			Period period = new Period();
			if (terserGet("/.PV1-44-1") != null && !terserGet("/.PV1-44-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.PV1-44-1"));
	        		period.setStart(date);
	        		encounter.setStatus(Encounter.EncounterState.INPROGRESS);
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
	        		encounter.setStatus(Encounter.EncounterState.FINISHED);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			encounter.setPeriod(period);
			log.debug("Pre-Specialties");
			if (terserGet("/.PV1-10-1") != null && !terserGet("/.PV1-10-1").isEmpty())
			{
				encounter.addType()
					.addCoding()
						.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES)
						.setCode(terserGet("/.PV1-10-1"));
			}
			if (terserGet("/.PV1-2") != null)
			{
				exchange.getIn().setHeader("FHIREpisodeType", terserGet("/.PV1-2"));
				switch (terserGet("/.PV1-2"))
				{
					case "O" : 
						encounter.setClass_(Encounter.EncounterClass.OUTPATIENT);
						if (terserGet("/.PV1-3-2") != null && !terserGet("/.PV1-3-2").isEmpty())
						{
							encounter.addType()
								.addCoding()
									.setSystem(TrustFHIRSystems.getURI_NHSOrg_CLINIC_CODE())
									.setCode(terserGet("/.PV1-3-2"))
									.setDisplay(terserGet("/.PV1-3-9"));
						}
						break;
					case "I" : encounter.setClass_(Encounter.EncounterClass.INPATIENT);
						break;
					case "E" : encounter.setClass_(Encounter.EncounterClass.EMERGENCY);
						break;
					default : encounter.setClass_(Encounter.EncounterClass.OTHER);
						break;
				}
			}
			log.info("Get Reference Material");
			if (terserGet("/.PV1-3-2") != null && !terserGet("/.PV1-3-2").isEmpty())
			{
				exchange.getIn().setHeader("FHIRLocation", terserGet("/.PV1-3-2"));
			}
			
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIREncounter", terserGet("/.PV1-19-1"));
				exchange.getIn().setHeader("FHIRAppointment", terserGet("/.PV1-19-1"));
			}
			
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
				case "A04":
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"POST");
					break;
				default:
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"PUT");	
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("#3 "+  exchange.getExchangeId() + " " + ex.getMessage() +  " "  + ex.getStackTrace() 
					+" Properties: " + exchange.getProperties().toString()
					+" Headers: " + exchange.getIn().getHeaders().toString() 
					+ " Message:" + exchange.getIn().getBody().toString());
		}
		exchange.getIn().setHeader("FHIRResource", "Encounter");
		String Response = ResourceSerialiser.serialise(encounter, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		//exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner/"+patient.getId());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
