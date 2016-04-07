package uk.co.mayfieldis.jorvik.UKFHIR;

import java.io.ByteArrayInputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.formats.XmlParser;

import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.DocumentReference.DocumentReferenceContextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;

public class FHIRDocumentReferenceProcess implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.UKFHIR.FHIRDocumentReferenceProcess.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// 
		try
		{
			DocumentReference documentReference = null;
			
				
			ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
			
			
			if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
			{
				JsonParser composer = new JsonParser();
				try
				{
					documentReference = (DocumentReference) composer.parse(xmlContentBytes);
				}
				catch(Exception ex)
				{
					log.error("#9 JSON Parse failed "+ex.getMessage());
				}
			}
			else
			{
				XmlParser composer = new XmlParser();
				try
				{
					documentReference = (DocumentReference)  composer.parse(xmlContentBytes);
				}
				catch(Exception ex)
				{
					log.error("#10 XML Parse failed "+ex.getMessage());
				}
			}
			
			// Stop http FHIR queries from processing input parameters
			exchange.getIn().removeHeaders("*");
			
			if (documentReference != null)
			{
				// remove any id passed in.
				
				documentReference.setId("");
				
				for (int f=0;f<documentReference.getContained().size();f++)
				{
					String resourceName = documentReference.getContained().get(f).getResourceType().toString();
					
					switch (resourceName) 
					{
						case "Patient":
							Patient patient = (Patient) documentReference.getContained().get(f);
							for (int g=0;g<patient.getIdentifier().size();g++)
							{
								if (patient.getIdentifier().get(g).getSystem().equals(NHSTrustFHIRCodeSystems.URI_PATIENT_DISTRICT_NUMBER))
								{
									exchange.getIn().setHeader("FHIRPatient",patient.getIdentifier().get(g).getValue() );
								}
							}
							documentReference.setSubject(null);
							break;
						case "Person":
							//log.debug("Found Person="+person.getId());
							break;
						case "Practitioner":
							Practitioner practitioner = (Practitioner) documentReference.getContained().get(f);
							for (int g=0;g<practitioner.getIdentifier().size();g++)
							{
								if (practitioner.getIdentifier().get(g).getSystem().equals(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS))
								{
									exchange.getIn().setHeader("FHIRPractitioner",practitioner.getIdentifier().get(g).getValue());
								}
								if (practitioner.getIdentifier().get(g).getSystem().equals(FHIRCodeSystems.URI_NHS_GMP_CODE))
								{
									exchange.getIn().setHeader("FHIRGP",practitioner.getIdentifier().get(g).getValue());
								}
							}
							break;
						case "Encounter":
							Encounter encounter = (Encounter) documentReference.getContained().get(f);
							for (int g=0;g<encounter.getIdentifier().size();g++)
							{
								if (encounter.getIdentifier().get(g).getSystem().equals(NHSTrustFHIRCodeSystems.uriCHFTActivityId))
								{
									exchange.getIn().setHeader("FHIREncounter",encounter.getIdentifier().get(g).getValue());
								}
							}
							if (documentReference.getContext() != null)
							{
								DocumentReferenceContextComponent comp = documentReference.getContext();
								comp.setEncounter(null);
							}
						break;
						
					}
				}
				// Extracted all data now remove contained
				documentReference.getContained().clear();
				documentReference.getAuthor().clear();
				
				// Place amended message into the exchange
				String Response = ResourceSerialiser.serialise(documentReference, ParserType.XML);
				exchange.getIn().setHeader("FHIRResource","/DocumentReference");
				exchange.getIn().setHeader("FHIRQuery","identifier="+documentReference.getIdentifier().get(0).getSystem()+"|"+documentReference.getIdentifier().get(0).getValue());
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
				exchange.getIn().setBody(Response);
			}
		
				
			
		}
		catch (Exception ex)
		{
			log.error(exchange.getExchangeId() + " "  + ex.getMessage() +" " + exchange.getProperties().toString());
		}
	}

}
