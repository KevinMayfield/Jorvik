package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
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
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;


public class ADTA01A04A08toEncounter implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEncounter.class);
	
	Terser terser = null;
	
	public ADTA01A04A08toEncounter(FhirContext ctx, Environment env, NHSTrustFHIRCodeSystems TrustFHIRSystems)
	{
		this.ctx = ctx;
		this.env = env;
		this.TrustFHIRSystems = TrustFHIRSystems;
	}
	
	private NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
	private Environment env;
	
	private FhirContext ctx;
	
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
			// Names PID.PatientName
			patient.addIdentifier(patientid);
			
			
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				log.debug("Activity ID "+terserGet("/.PV1-19-1"));
				
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
				Coding code = new Coding();
				switch (terserGet("/.PV1-2"))
				{
					case "O" : 
						code.setCode("outpatient");
						encounter.setClass_(code);
						if (terserGet("/.PV1-3-2") != null && !terserGet("/.PV1-3-2").isEmpty())
						{
							encounter.addType()
								.addCoding()
									.setSystem(TrustFHIRSystems.getURI_NHSOrg_CLINIC_CODE())
									.setCode(terserGet("/.PV1-3-2"))
									.setDisplay(terserGet("/.PV1-3-9"));
						}
						break;
					case "I" : 
						code.setCode("inpatient");
						encounter.setClass_(code);
						break;
					case "E" : 
						code.setCode("emergency");
						encounter.setClass_(code);
						break;
					default : 
						code.setCode("other");
						encounter.setClass_(code);
						break;
				}
			}
			log.debug("Get Reference Material");
			
			
			
			if (terserGet("/.PV1-3-1") != null && !terserGet("/.PV1-3-1").isEmpty())
			{
				//exchange.getIn().setHeader("FHIRLocation", terserGet("/.PV1-3-1").replace(' ', '-'));
				
				Location parentLoc = new Location();
				parentLoc.setId(IdDt.newRandomUuid());
				parentLoc.addIdentifier()
					.setValue(terserGet("/.PV1-3-1").replace(' ', '-'))
					.setSystem(TrustFHIRSystems.geturiNHSOrgLocation());
				
				// Create reference in main resource
				encounter.addLocation().setLocation(new Reference(parentLoc.getId()));
				
				bundle.addEntry()
				   .setFullUrl(parentLoc.getId())
				   .setResource(parentLoc)
				   .getRequest()
				      .setUrl("Location")
				      .setIfNoneExist("Location?identifier="+parentLoc.getIdentifier().get(0).getSystem()+"|"+parentLoc.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
			
				//exchange.getIn().setHeader("FHIRAppointment", terserGet("/.PV1-19-1"));
				Appointment appt = new Appointment();
				appt.setId(IdDt.newRandomUuid());
				appt.addIdentifier()
					.setValue(terserGet("/.PV1-19-1"))
					.setSystem(TrustFHIRSystems.geturiNHSOrgAppointmentId());
				
				// Create reference in main resource
				encounter
					.setAppointment(new Reference(appt.getId()));
				
				bundle.addEntry()
				   .setFullUrl(appt.getId())
				   .setResource(appt)
				   .getRequest()
				      .setUrl("Appointment")
				      .setIfNoneExist("Appointment?identifier="+appt.getIdentifier().get(0).getSystem()+"|"+appt.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			
			if (terserGet("/.PV1-50-1") != null && !terserGet("/.PV1-50-1").isEmpty())
			{
				//exchange.getIn().setHeader("FHIREpisode", terserGet("/.PV1-50-1"));
				EpisodeOfCare episode = new EpisodeOfCare();
				episode.setId(IdDt.newRandomUuid());
				episode.addIdentifier()
					.setValue(terserGet("/.PV1-50-1"))
					.setSystem(env.getProperty("ORG.TrustEpisodeOfCare"));
				
				// Create reference in main resource
				encounter
					.addEpisodeOfCare()
					.setReference(episode.getId());
				
				bundle.addEntry()
				   .setFullUrl(episode.getId())
				   .setResource(episode)
				   .getRequest()
				      .setUrl("EpisodeOfCare")
				      .setIfNoneExist("EpisodeOfCare?identifier="+episode.getIdentifier().get(0).getSystem()+"|"+episode.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			
			if (terserGet("/.PV1-3-4") != null && !terserGet("/.PV1-3-4").isEmpty())
			{
				//exchange.getIn().setHeader("FHIROrganisationCode", terserGet("/.PV1-3-4"));
				Organization parentOrg = new Organization();
				parentOrg.setId(IdDt.newRandomUuid());
				parentOrg.addIdentifier()
					.setValue(terserGet("/.PV1-3-4"))
					.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
				
				// Create reference in main resource
				encounter.setServiceProvider(new Reference(parentOrg.getId()));
				
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
				Practitioner consultant = new Practitioner();
				consultant.setId(IdDt.newRandomUuid());
				consultant.addIdentifier()
					.setValue(terserGet("/.PV1-9-1"))
					.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
				
				// Create reference in main resource
				encounter.addParticipant().setIndividual(new Reference(consultant.getId()));
				
				bundle.addEntry()
				   .setFullUrl(consultant.getId())
				   .setResource(consultant)
				   .getRequest()
				      .setUrl("Practitioner")
				      .setIfNoneExist("Practitioner?identifier="+consultant.getIdentifier().get(0).getSystem()+"|"+consultant.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.POST);
			}
			
			//
		
			
			// Create reference in main resource
			encounter.setPatient(new Reference(patient.getId()));
			
			bundle.addEntry()
			   .setFullUrl(patient.getId())
			   .setResource(patient)
			   .getRequest()
			      .setUrl("Patient")
			      .setIfNoneExist("Patient?identifier="+patient.getIdentifier().get(0).getSystem()+"|"+patient.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
			
			// Master resource
			bundle.addEntry()
			   .setResource(encounter)
			   .getRequest()
			      .setUrl("Encounter?identifier="+encounter.getIdentifier().get(0).getSystem()+"|"+encounter.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.PUT);
			
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
			
			exchange.getIn().setHeader("FHIRResource","/");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			exchange.getIn().setBody(Response);
			/*
			switch (terserGet("/.MSH-9-2"))
			{
				case "A01":
				case "A04":
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
		exchange.getIn().setHeader("FHIRResource", "Encounter");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
