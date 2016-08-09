package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Identifier;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;


public class ADTA01A04A08toEpisodeOfCare implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEpisodeOfCare.class);
	
	Terser terser = null;
	
	public ADTA01A04A08toEpisodeOfCare(FhirContext ctx, Environment env)
	{
		this.ctx = ctx;
		this.env = env;
		//this.TrustFHIRSystems = TrustFHIRSystems;
	}
	
	private Environment env;
	
	private FhirContext ctx;
	
	//private NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
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
		Patient patient = new Patient();
		patient.setId(IdDt.newRandomUuid());
		Identifier patientid = new Identifier();
		Bundle bundle = new Bundle();
		
		bundle.setType(BundleType.TRANSACTION);
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
					log.debug("PID "+code+" "+value);
					
					switch (code)
					{
						case "PAS":
							if (exchange.getIn().getHeader("FHIRPatient") ==null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
								patientid
									.setValue(value)
									.setSystem(env.getProperty("ORG.PatientIdentifier"+code));
							}
							break;
						case "NHS":
							if (exchange.getIn().getHeader("FHIRPatient") ==null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
								patientid
									.setValue(value)
									.setSystem(env.getProperty("ORG.PatientIdentifier"+code));
							}
							break;
						default:
							exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							patientid
								.setValue(value)
								.setSystem(env.getProperty("ORG.PatientIdentifier"+code));
							break;
					}
				}
			}
			log.debug("FHIRPatient  = "+exchange.getIn().getHeader("FHIRPatient").toString());
			patient.addIdentifier(patientid);
			
			// Names PID.PatientName
			log.debug("Activity ID");
			if (terserGet("/.PV1-50-1") != null && !terserGet("/.PV1-50-1").isEmpty())
			{
				episode.addIdentifier()
					.setSystem(env.getProperty("ORG.TrustEpisodeOfCare"))
					.setValue(terserGet("/.PV1-50-1"));
				exchange.getIn().setHeader("FHIREpisode", terserGet("/.PV1-50-1"));
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
			if ((terserGet("/.ZU3-4") != null) && (terserGet("/.ZU3-4").equals("1")))
			{
				episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.FINISHED);
			}
			if (terserGet("/.MSH-9-2") != null)
			{
				if (terserGet("/.MSH-9-2").equals("A03"))
				{
						if ((terserGet("/.PV1-2") != null) && (terserGet("/.PV1-2").equals("O")))  
						{
							// Null see ZU3 code
						}
						else
						{
							// Only for non outpatients
							episode.setStatus(EpisodeOfCare.EpisodeOfCareStatus.FINISHED);
						}
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
			/*			
			if (terserGet("/.PV1-50-1") != null && !terserGet("/.PV1-50-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIREpisode", terserGet("/.PV1-50-1"));
			}
			*/
			if (terserGet("/.PV1-3-4") != null && !terserGet("/.PV1-3-4").isEmpty())
			{
				//exchange.getIn().setHeader("FHIROrganisationCode", terserGet("/.PV1-3-4"));
				Organization parentOrg = new Organization();
				parentOrg.setId(IdDt.newRandomUuid());
				parentOrg.addIdentifier()
					.setValue(terserGet("/.PV1-3-4"))
					.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
				
				// Create reference in main resource
				
				episode.setManagingOrganization(new Reference(parentOrg.getId()));
				bundle.addEntry()
				   .setFullUrl(parentOrg.getId())
				   .setResource(parentOrg)
				   .getRequest()
				      .setUrl("Organization")
				      .setIfNoneExist("Organization?identifier="+parentOrg.getIdentifier().get(0).getSystem()+"|"+parentOrg.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			
			if (terserGet("/.PV1-9-1") != null && !terserGet("/.PV1-9-1").isEmpty())
			{
				//exchange.getIn().setHeader("FHIRPractitioner", terserGet("/.PV1-7-1"));
				Practitioner consultant = new Practitioner();
				consultant.setId(IdDt.newRandomUuid());
				consultant.addIdentifier()
					.setValue(terserGet("/.PV1-7-1"))
					.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
				
				// Create reference in main resource
				
				if (episode.getTeam().size() == 0)
				{
					episode.addTeam(new Reference(consultant.getId()));
				}
				else
				{
					episode.getTeam().set(0,new Reference(consultant.getId()));
				}
				
				bundle.addEntry()
				   .setFullUrl(consultant.getId())
				   .setResource(consultant)
				   .getRequest()
				      .setUrl("Practitioner")
				      .setIfNoneExist("Practitioner?identifier="+consultant.getIdentifier().get(0).getSystem()+"|"+consultant.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			episode.setPatient(new Reference(patient.getId()));
			
			bundle.addEntry()
			   .setFullUrl(patient.getId())
			   .setResource(patient)
			   .getRequest()
			      .setUrl("Patient")
			      .setIfNoneExist("Patient?identifier="+patient.getIdentifier().get(0).getSystem()+"|"+patient.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
			
			// Master resource
			bundle.addEntry()
			   .setResource(episode)
			   .getRequest()
			      .setUrl("EpisodeOfCare?identifier="+episode.getIdentifier().get(0).getSystem()+"|"+episode.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.PUT);
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
			
			exchange.getIn().setHeader("FHIRResource","/");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			exchange.getIn().setBody(Response);
			
			/*
			switch (terserGet("/.MSH-9-2"))
			{
				case "A01":
				case "A08":
				case "A03":
					exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
					break;
				default:
					exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");	
			}
			*/
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		exchange.getIn().setHeader("FHIRResource", "EpisodeOfCare");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
	
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
