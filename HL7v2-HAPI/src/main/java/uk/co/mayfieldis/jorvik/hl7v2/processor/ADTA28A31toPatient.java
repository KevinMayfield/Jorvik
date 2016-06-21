		package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.ContactPoint;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.camel.ResourceSerialiser;

public class ADTA28A31toPatient implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA28A31toPatient.class);
	
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
		
		/*
		HapiContext context = new DefaultHapiContext();
		
		CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.4");
		
		context.setModelClassFactory(mcf);
		PipeParser parser = context.getPipeParser();
		
		msg = (ca.uhn.hl7v2.model.v24.message.ADT_A05) parser.parse(exchange.getIn().getBody(Message.class));
		*/
		Message message = exchange.getIn().getBody(Message.class);
		
		Patient patient = new Patient();
		
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
					//log.info("Item "+f+" is not empty "+terserGet("/.PID-3("+f+")-1"));
					
					switch (code)
					{
						case "PAS":
							patient.addIdentifier()
								.setSystem(env.getProperty("ORG.PatientIdentifierPAS"))
								.setValue(value);
							if (exchange.getIn().getHeader("FHIRPatient") !=null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							break;
						case "RWY":
							patient.addIdentifier()
								.setSystem(env.getProperty("ORG.PatientIdentifierRWY"))
								.setValue(value);
							// Want to search on main trust identifier 
							exchange.getIn().setHeader("FHIRPatient", env.getProperty("ORG.PatientIdentifierRWY")+"|"+value);
							break;
						case "MRN":
							patient.addIdentifier()
								.setSystem(env.getProperty("ORG.PatientIdentifierUN1"))
								.setValue(value);
							break;
						case "NHS":
							patient.addIdentifier()
								.setSystem(env.getProperty("ORG.PatientIdentifierNHS"))
								.setValue(value);
							if (exchange.getIn().getHeader("FHIRPatient") ==null || exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
							{
								exchange.getIn().setHeader("FHIRPatient",env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
							}
							// May need to add search on NHS number but for trust systems  NHSNumber is transient.
							break;
						default:
							if (!(env.getProperty("ORG.PatientIdentifier"+code)).isEmpty())
							{
								patient.addIdentifier()
									.setSystem(env.getProperty("ORG.PatientIdentifier"+code))
									.setValue(value);
								if (exchange.getIn().getHeader("FHIRPatient").toString().isEmpty())
								{
									exchange.getIn().setHeader("FHIRPatient", env.getProperty("ORG.PatientIdentifier"+code)+"|"+value);
								}
							}
					}
				}
			}
			log.debug("FHIRPatient  = "+exchange.getIn().getHeader("FHIRPatient").toString());
			// Names PID.PatientName
			log.debug("Patient Name");
			if ((terserGet("/.PID-5-1") != null && !terserGet("/.PID-5-1").isEmpty() ) || (terserGet("/.PID-5-2") != null && !terserGet("/.PID-5-2").isEmpty()))
			{
				patient.addName()
	    		.addFamily(terserGet("/.PID-5-1"))
	    		.addGiven(terserGet("/.PID-5-2"))
	    		.addPrefix(terserGet("/.PID-5-5"));
			}
			log.debug("Patient Date Of Birth");
			// Date Of Birth
			if (terserGet("/.PID-7-1") != null && !terserGet("/.PID-7-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
				
	        	try {
	        		Date dob;
	        		
	        		dob = fmt.parse(terserGet("/.PID-7-1").substring(0, 8));
	        		patient.setBirthDate(dob);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			// Gender
			log.debug("Patient Gender");
			if (terserGet("/.PID-8") != null && !terserGet("/.PID-8").isEmpty())
			{
				switch (terserGet("/.PID-8"))
	        	{
	        		case "1":  
	        			patient.setGender(AdministrativeGender.MALE);
	        			break;
	        		case "2":  
	        			patient.setGender(AdministrativeGender.FEMALE);
	        			break;
	        		case "9":  
	        			patient.setGender(AdministrativeGender.OTHER);
	        			break;
	        		case "0":  
	        			patient.setGender(AdministrativeGender.UNKNOWN);
	        			break;
	        		default:
	        			patient.setGender(AdministrativeGender.UNKNOWN);
	        			break;
	        	}
			}
			else
			{
				patient.setGender(AdministrativeGender.UNKNOWN);
			}
			// Address
			log.debug("Patient Address");
			if ((terserGet("/.PID-11-1") !=null && !terserGet("/.PID-11-1").isEmpty()) || (terserGet("/.PID-11-5") != null && !terserGet("/.PID-11-5").isEmpty()))
		    {
	        	Address address = patient.addAddress();
	        	
	        	if (terserGet("/.PID-11-1") != null && !terserGet("/.PID-11-1").replaceAll("\"","").isEmpty())
	        	{
	        		address.addLine(terserGet("/.PID-11-1").replaceAll("\"",""));
	        	}
	        	if (terserGet("/.PID-11-2") != null && !terserGet("/.PID-11-2").replaceAll("\"","").isEmpty())
	        	{
	        		address.addLine(terserGet("/.PID-11-2").replaceAll("\"",""));
	        	}
	        	if (terserGet("/.PID-11-3") != null && !terserGet("/.PID-11-3").replaceAll("\"","").isEmpty())
	        	{
	        		address.setCity(terserGet("/.PID-11-3").replaceAll("\"",""));
	        	}
	        	if (terserGet("/.PID-11-4") != null && !terserGet("/.PID-11-4").replaceAll("\"","").isEmpty())
	        	{
	        		address.setState(terserGet("/.PID-11-4").replaceAll("\"",""));
	        	}
	        	if (terserGet("/.PID-11-5") != null && !terserGet("/.PID-11-5").replaceAll("\"","").isEmpty())
	        	{
	        		address.setPostalCode(terserGet("/.PID-11-5"));
	        	}
		     }
			// Phone numbers
			log.debug("Patient Telecom");
			for (int f=0;f<maxRepitions;f++)
			{
				String code =null;
				String value =null;
				String value1 =null;
				if (f==0)
				{
					code =terserGet("/.PID-13-2");
					value =terserGet("/.PID-13-1");
					value1 =terserGet("/.PID-13-3");
				}
				else
				{
					code =terserGet("/.PID-13("+f+")-2");
					value =terserGet("/.PID-13("+f+")-1");
					value1 =terserGet("/.PID-13("+f+")-3");
				}
				if (value !=null && !value.isEmpty())
				{
					ContactPoint contactPoint =patient.addTelecom(); 
					if (code == null || code.isEmpty())
					{
						code="PRN";
					}
					switch (code)
					{
						case "PRN":
							contactPoint
								.setValue(value)
								.setSystem(ContactPointSystem.PHONE);
							break;
						case "ORN":
							contactPoint
								.setValue(value)
								.setSystem(ContactPointSystem.PHONE);
							break;
					}
					if (value1 !=null && !value1.isEmpty())
					{
						switch (value1)
						{
							case "PH":
								contactPoint
									.setUse(ContactPointUse.HOME);
								break;
							case "CP":
								contactPoint
									.setUse(ContactPointUse.MOBILE);
								break;
						}
					}
				}
			}
			for (int f=0;f<maxRepitions;f++)
			{
				String code =null;
				String value =null;
				String value1 =null;
				
				if (f==0)
				{
					code =terserGet("/.PID-14-2");
					value =terserGet("/.PID-14-1");
					value1 =terserGet("/.PID-14-3");
				}
				else
				{
					code =terserGet("/.PID-14("+f+")-2");
					value =terserGet("/.PID-14("+f+")-1");
					value1 =terserGet("/.PID-14("+f+")-3");
				}
				if (value !=null && !value.isEmpty())
				{
					ContactPoint contactPoint =patient.addTelecom(); 
					if (code == null || code.isEmpty())
					{
						code="WPN";
					}
					switch (code)
					{
						case "PRN":
							contactPoint
								.setValue(value)
								.setSystem(ContactPointSystem.PHONE);
							break;
						case "ORN":
							contactPoint
								.setValue(value)
								.setSystem(ContactPointSystem.PHONE);
							break;
						case "WPN":
							contactPoint
								.setValue(value)
								.setSystem(ContactPointSystem.PHONE)
								.setUse(ContactPointUse.WORK);
							break;
					}
					
					if (value1 !=null && !value1.isEmpty())
					{
						switch (value1)
						{
							case "PH":
								contactPoint
									.setUse(ContactPointUse.HOME);
								break;
							case "CP":
								contactPoint
									.setUse(ContactPointUse.MOBILE);
								break;
						}
					}
				}
			}
			// Date Of Death
			log.debug("Patient DoD");
			if (terserGet("/.PID-29-1") != null && !terserGet("/.PID-29-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date dod;
	        		dod = fmt.parse(terserGet("/.PID-29-1"));
	        		DateType dt = new DateType();
	        		dt.setValue(dod);
	        		patient.setDeceased(dt);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			log.debug("Patient Org");
			if (terserGet("/.PD1-3-3") != null && !terserGet("/.PD1-3-3").isEmpty())
			{
				exchange.getIn().setHeader("FHIROrganisationCode",terserGet("/.PD1-3-3"));
			}
			log.debug("Patient GP");
			if (terserGet("/.PD1-4-1") != null && !terserGet("/.PD1-4-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIRGP", terserGet("/.PD1-4-1"));
			}
			if (terserGet("/.MSH-9-2") != null && terserGet("/.MSH-9-2").equals("A31"))
			{
				exchange.getIn().setHeader(Exchange.HTTP_PATH,"PUT");
			}
			else
			{
				exchange.getIn().setHeader(Exchange.HTTP_PATH,"POST");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("#3 "+ exchange.getExchangeId() + " "  + ex.getMessage() 
					+" Properties: " + exchange.getProperties().toString()
					+" Headers: " + exchange.getIn().getHeaders().toString() 
					+ " Message:" + exchange.getIn().getBody().toString());
		}
		
		String Response = ResourceSerialiser.serialise(patient, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		//exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner/"+patient.getId());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
