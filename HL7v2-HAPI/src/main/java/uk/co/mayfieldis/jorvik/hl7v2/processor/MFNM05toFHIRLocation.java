package uk.co.mayfieldis.jorvik.hl7v2.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;

public class MFNM05toFHIRLocation implements Processor { 

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.MFNM05toFHIRLocation.class);
	
	Terser terser = null;
	
	public MFNM05toFHIRLocation(FhirContext ctx, NHSTrustFHIRCodeSystems TrustFHIRSystems)
	{
		this.ctx = ctx;
		
		this.TrustFHIRSystems = TrustFHIRSystems;
	}
	
	private NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
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
	public void process(Exchange exchange) throws HL7Exception  {
		
		Message message = exchange.getIn().getBody(Message.class);
		
		Location location = new Location();
		
		// Use Terser as code is more readable
		terser = new Terser(message);
			
		try
		{
			if (terserGet("/.LOC-2") != null && !terserGet("/.LOC-2").isEmpty())
			{
				location.setName(terserGet("/.LOC-2"));
			}
			
			location.addIdentifier()
				.setValue(terser.get("/.LOC-1-1"))
				.setSystem(TrustFHIRSystems.geturiNHSOrgLocation());
			
			if (terserGet("/.LOC-3-1") != null && !terserGet("/.LOC-3-1").isEmpty())
			{
				switch(terserGet("/.LOC-3-1"))
				{
					case "C" :
						CodeableConcept type = new CodeableConcept();
						type.addCoding()
							.setCode("OF")
							.setSystem("http://hl7.org/fhir/ValueSet/v3-ServiceDeliveryLocationRoleType");
						location.setType(type);
						break;
				}
			}
			
			if (terserGet("/.LOC-1-5") != null && !terserGet("/.LOC-1-5").isEmpty())
			{
				exchange.getIn().setHeader("FHIROrganisationCode",terserGet("/.LOC-1-5").substring(0,3));
			}
			
			/*
			ServiceTypeComponent serviceType = healthcareService.addServiceType();
			if (terserGet("/.LOC-4-2") != null && !terser.get("/.LDP-4-2").isEmpty())
			{
				CodeableConcept localSpecialty = new CodeableConcept();
				localSpecialty.addCoding()
					.setCode(terser.get("/.LDP-4-1"))
					.setSystem(NHSTrustFHIRCodeSystems.URI_CHFT_SPECIALTY);
				serviceType.addSpecialty(localSpecialty);
			}
			
			if (!terser.get("/.LDP-4-2").isEmpty())
			{
				CodeableConcept NHSSpecialty = new CodeableConcept();
				NHSSpecialty.addCoding()
					.setCode(terser.get("/.LDP-4-2"))
					.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
					
				serviceType.addSpecialty(NHSSpecialty);
			}
			*/
			if (terserGet("/.LOC-1-4") != null && !terserGet("/.LOC-1-4").isEmpty())
			{
				exchange.getIn().setHeader("FHIRLocation",terserGet("/.LOC-1-4"));
			}	
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(location);
			//String Response = ResourceSerialiser.serialise(location, ParserType.XML);
			
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
			
			if (terser.get("/.MFI-3").equals("UPD"))
			{
				exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
			}
			else
			{
				exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
			}
			
			exchange.getIn().setHeader("FHIRResource","/Location");
			exchange.getIn().setHeader("FHIRQuery","identifier="+location.getIdentifier().get(0).getSystem()+"|"+location.getIdentifier().get(0).getValue());
			
			exchange.getIn().setBody(Response);
			
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
		}
		
	}

}
