package uk.co.mayfieldis.jorvik.hl7v2.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;



public class MFNM02toFHIRPractitioner implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.MFNM02toFHIRPractitioner.class);
	
	Terser terser = null;
	
	public NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
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
	
	public void process(Exchange exchange) throws HL7Exception 
	{
		Message message = exchange.getIn().getBody(Message.class);
		// Use Terser as code is more readable
		terser = new Terser(message);
		
		Practitioner practitioner = new Practitioner();
		HumanName name = new HumanName();
		if (terserGet("/.STF-3-1") != null && !terserGet("/.STF-3-1").isEmpty())
		{
			name.addFamily(terserGet("/.STF-3-1"));
		}
		if (terserGet("/.STF-3-2") != null && !terserGet("/.STF-3-2").isEmpty())
		{
			name.addGiven(terserGet("/.STF-3-2"));
		}
		if (terserGet("/.STF-3-5") != null && !terserGet("/.STF-3-5").isEmpty())
		{
			name.addPrefix(terserGet("/.STF-3-5"));
		}
		practitioner.setName(name);
		
		if (terserGet("/.STF-1") != null && !terserGet("/.STF-1").isEmpty())
		{
			practitioner.addIdentifier()
				.setValue(terserGet("/.STF-1"))
				.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
		}
		if (terserGet("/.STF-2") != null && !terserGet("/.STF-2").isEmpty())
		{
			practitioner.addIdentifier()
				.setValue(terserGet("/.STF-2"))
				.setSystem(TrustFHIRSystems.getURI_NHSOrg_PAS_CONSULTANT_CODE());
		}
		if (terserGet("/.PRA-5-1") != null && !terserGet("/.PRA-5-1").isEmpty())
		{
			practitioner.addPractitionerRole()
			.addSpecialty()
				.addCoding()
					.setCode(terserGet("/.PRA-5-1"))
					.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
		}
		
		exchange.getIn().setHeader("FHIROrganisationCode","RWY");
		
		
		String Response = ResourceSerialiser.serialise(practitioner, ParserType.XML);
		
		exchange.getIn().setHeader("FHIRResource","/Practitioner");
		exchange.getIn().setHeader("FHIRQuery","identifier="+practitioner.getIdentifier().get(0).getSystem()+"|"+practitioner.getIdentifier().get(0).getValue());
		
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		
		if (terserGet("/.MFI-3").equals("UPD"))
		{
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
		}
		else
		{
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
		}
		
		exchange.getIn().setBody(Response);
		
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}



}
