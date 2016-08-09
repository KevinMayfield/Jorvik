package uk.co.mayfieldis.jorvik.UKFHIR;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContextComponent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;


public class FHIRDocumentReferenceProcess implements Processor {

	public FHIRDocumentReferenceProcess (FhirContext ctx, NHSTrustFHIRCodeSystems TrustFHIRSystems)
	{
		this.ctx = ctx;
	
		this.TrustFHIRSystems = TrustFHIRSystems;
	}
	
	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.UKFHIR.FHIRDocumentReferenceProcess.class);
	
	private NHSTrustFHIRCodeSystems TrustFHIRSystems;
	
	
	private FhirContext ctx;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// 
		try
		{
			DocumentReference documentReference = null;
			Bundle bundle = new Bundle();
			
			bundle.setType(BundleType.TRANSACTION);
				
			//ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
			Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
			
			
			if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
			{
				//JsonParser parser = new JsonParser();
				IParser parser = ctx.newJsonParser();
				try
				{
					documentReference = parser.parseResource(DocumentReference.class,reader);
				}
				catch(Exception ex)
				{
					log.error("#9 JSON Parse failed "+ex.getMessage());
				}
			}
			else
			{
				// XmlParser parser = new XmlParser();
				IParser parser = ctx.newXmlParser();
				try
				{
					documentReference = parser.parseResource(DocumentReference.class,reader);
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
								String code = patient.getIdentifier().get(g).getSystem();
								
								if (code.equals(TrustFHIRSystems.getURI_PATIENT_OTHER_NUMBER()))
								{
										exchange.getIn().setHeader("FHIRPatient",TrustFHIRSystems.getURI_PATIENT_OTHER_NUMBER()+"|"+patient.getIdentifier().get(g).getValue() );
								}
								else if (code.equals(TrustFHIRSystems.getURI_PATIENT_HOSPITAL_NUMBER()))
								{
									if ((exchange.getIn().getHeader("FHIRPatient") == null) || (exchange.getIn().getHeader("FHIRPatient").toString().isEmpty()))
									{
										exchange.getIn().setHeader("FHIRPatient",TrustFHIRSystems.getURI_PATIENT_HOSPITAL_NUMBER()+"|"+patient.getIdentifier().get(g).getValue() );
									}
								}
								else if (code.equals(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND))
								{
									if ((exchange.getIn().getHeader("FHIRPatient") == null) || (exchange.getIn().getHeader("FHIRPatient").toString().isEmpty()))
									{
										exchange.getIn().setHeader("FHIRPatient",FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND+"|"+patient.getIdentifier().get(g).getValue() );
									}
								}
							}
							patient.setId(IdDt.newRandomUuid());
							documentReference.setSubject(new Reference(patient.getId()));
							bundle.addEntry()
							   .setFullUrl(patient.getId())
							   .setResource(patient)
							   .getRequest()
							      .setUrl("Patient")
							      .setIfNoneExist("Patient?identifier="+exchange.getIn().getHeader("FHIRPatient"))
							      .setMethod(HTTPVerb.POST);
							break;
						case "Person":
							//log.debug("Found Person="+person.getId());
							break;
						case "Practitioner":
							Practitioner practitioner = (Practitioner) documentReference.getContained().get(f);
							String pracIdent = new String();
							for (int g=0;g<practitioner.getIdentifier().size();g++)
							{
								if (practitioner.getIdentifier().get(g).getSystem().equals(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS))
								{
									exchange.getIn().setHeader("FHIRPractitioner",practitioner.getIdentifier().get(g).getValue());
									pracIdent = FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS +"|"+practitioner.getIdentifier().get(g).getValue();
								}
								if (practitioner.getIdentifier().get(g).getSystem().equals(FHIRCodeSystems.URI_NHS_GMP_CODE))
								{
									exchange.getIn().setHeader("FHIRGP",practitioner.getIdentifier().get(g).getValue());
									pracIdent = FHIRCodeSystems.URI_NHS_GMP_CODE +"|"+practitioner.getIdentifier().get(g).getValue();
								}
							}
							practitioner.setId(IdDt.newRandomUuid());
							documentReference.getAuthor().clear();
							documentReference.addAuthor(new Reference(practitioner.getId()));
							bundle.addEntry()
							   .setFullUrl(practitioner.getId())
							   .setResource(practitioner)
							   .getRequest()
							      .setUrl("Practitioner")
							      .setIfNoneExist("Practitionert?identifier="+pracIdent)
							      .setMethod(HTTPVerb.POST);
							break;
						case "Encounter":
							Encounter encounter = (Encounter) documentReference.getContained().get(f);
							for (int g=0;g<encounter.getIdentifier().size();g++)
							{
								if (encounter.getIdentifier().get(g).getSystem().equals(TrustFHIRSystems.geturiNHSOrgActivityId()))
								{
									exchange.getIn().setHeader("FHIREncounter",encounter.getIdentifier().get(g).getValue());
								}
							}
							if (documentReference.getContext() != null)
							{
								DocumentReferenceContextComponent comp = documentReference.getContext();
								comp.setEncounter(null);
							
								encounter.setId(IdDt.newRandomUuid());
								comp.setEncounter(new Reference(encounter.getId()));
								
								bundle.addEntry()
								   .setFullUrl(encounter.getId())
								   .setResource(encounter)
								   .getRequest()
								      .setUrl("Encounter")
								      .setIfNoneExist("Encounter?identifier="+TrustFHIRSystems.geturiNHSOrgActivityId()+"|"+exchange.getIn().getHeader("FHIREncounter"))
								      .setMethod(HTTPVerb.POST);
							}
						break;
						
					}
				}
				// Extracted all data now remove contained
				documentReference.getContained().clear();
				// Master resource
				bundle.addEntry()
				   .setResource(documentReference)
				   .getRequest()
				      .setUrl("DocumentReference?identifier="+documentReference.getIdentifier().get(0).getSystem()+"|"+documentReference.getIdentifier().get(0).getValue())
				      .setMethod(HTTPVerb.PUT);
				
				
				String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
				
				exchange.getIn().setHeader("FHIRResource","/");
				exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
				exchange.getIn().setBody(Response);
				
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
				exchange.getIn().setBody(Response);
			}
		
				
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
			//log.error(exchange.getExchangeId() + " "  + ex.getMessage() +" " + exchange.getProperties().toString());
		}
	}

}
