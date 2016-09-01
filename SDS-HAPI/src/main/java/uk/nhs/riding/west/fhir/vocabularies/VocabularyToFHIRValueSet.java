package uk.nhs.riding.west.fhir.vocabularies;

import java.io.ByteArrayInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.math.NumberUtils;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.Enumerations.ConformanceResourceStatus;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ValueSetComposeComponent;
import ca.uhn.fhir.context.FhirContext;


public class VocabularyToFHIRValueSet implements Processor {

	public VocabularyToFHIRValueSet(FhirContext ctx)
	{
		this.ctx = ctx;
	}
	private FhirContext ctx;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
		JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
        
		Vocabulary vocab = (Vocabulary) unmarshaller.unmarshal(xmlContentBytes);
		
		
		
		// Stop http FHIR queries from processing input parameters
		//exchange.getIn().removeHeaders("*");
		
		String vocabName = vocab.getName();
		exchange.getIn().setHeader("ActiveStatus", vocab.getStatus());
		switch(vocab.getStatus())
		{
			case "active" :
			case "Active" :
			case "created" :
				exchange.getIn().setHeader("ActiveStatus", "active" );
				break;
			default:
		}
		String system = new String();
		
		if (vocab.getId().contains("."))
		{
			system= "urn:oid:"+vocab.getId();
			if (vocab.getId().contains("2.16.840.1.113883.2.1.9") || (vocab.getId().contains("2.16.840.1.113883.12.")))
			{
				vocabName = "NHSITK-v2-"+vocabName;
			}
			else if (vocab.getId().contains("2.16.840.1.113883.2.1."))
			{
				if (vocab.getId().equals("2.16.840.1.113883.2.1.3.2.4.15"))
				{
					system="http://snomed.info/sct";
				}
				vocabName = "NHSITK-v3-"+vocabName;
			}
			else
			{
				/* Idea behind this section was to ignore vocabularies with international OID's 
				 * However many UK vocabs use international OID's. So processing regardless.
				 * This only applies to v2 vocabularies.
				 */
				vocabName = "NHSITK-v2-"+vocabName;
				/*
				vocabName = "NotUK-"+vocabName;
				exchange.getIn().setHeader("ActiveStatus", "NotUK" );
				*/
			}
		}
		else if (NumberUtils.isNumber(vocab.getId())) 
		{
			system = "http://snomed.info/sct";
			vocabName = "NHSITK-SCT-"+vocabName;
		}
		else
		{
			// May not be robust
			system = "http://snomed.info/sct";
		}
		
		exchange.getIn().setHeader(Exchange.FILE_NAME, vocabName+".XML");
		
		String idStr = vocabName.replace(" ","-").toLowerCase();
		if (idStr.length()>64)
		{
			idStr = idStr.substring(0, 64);
		}
		else
		{
			idStr = idStr.substring(0, idStr.length());
		}
		
		if (system.equals("http://snomed.info/sct"))
		{
			ValueSet valueSet = new ValueSet();
			ConceptSetComponent concepts = new ConceptSetComponent();
			concepts.setSystem(system);
			
			Extension extension = new Extension();
			extension.setUrl("http://hl7.org/fhir/StructureDefinition/valueset-oid");
			extension.setValue(new UriType("urn:oid:"+vocab.getId()));
			valueSet.addExtension(extension);
			
			extension = new Extension();
			extension.setUrl("http://west.riding.nhs.uk/fhir/StructureDefinition/vocabulary-filename");
			extension.setValue(new StringType(exchange.getIn().getHeader(Exchange.FILE_NAME).toString()));
			valueSet.addExtension(extension);
			
			exchange.getIn().setHeader("ActiveStatus", vocab.getStatus());
			switch(vocab.getStatus())
			{
				case "active" :
				case "Active" :
				case "created" :
					valueSet.setStatus(ConformanceResourceStatus.ACTIVE);
					break;
				case "superseded" :
					valueSet.setStatus(ConformanceResourceStatus.RETIRED);
					break;
				default:
					valueSet.setStatus(ConformanceResourceStatus.NULL);
			}
			valueSet.setId(idStr);
			
			if (vocab.getDescription() !=null)
			{
				// Could do with a cleaner conversion method - also need to check xsd
				String description = "";
				for (int h = 0;h<vocab.getDescription().getP().size();h++)
				{
					description = description + vocab.getDescription().getP().get(h).toString()+" ";
				}
				valueSet.setDescription(description);
			}
			else
			{
				valueSet.setDescription(vocabName);
			};	
			valueSet.setVersion(vocab.getVersion());
			valueSet.setName(vocabName);
			
			for (int f=0;f<vocab.getConcept().size();f++)
			{
				ConceptReferenceComponent code = new ConceptReferenceComponent();
				code.setCode(vocab.getConcept().get(f).getCode().toString());
				for (int g=0;g<vocab.getConcept().get(f).getDisplayName().size();g++)
				{
					if (vocab.getConcept().get(f).getDisplayName().get(g).getType() != null)
					{
						if (vocab.getConcept().get(f).getDisplayName().get(g).getType().equals("PT"))
						{
							code.setDisplay(vocab.getConcept().get(f).getDisplayName().get(g).getValue());
						}
					}
					else
					{
						code.setDisplay(vocab.getConcept().get(f).getDisplayName().get(g).getValue());
						
					}
				}
				concepts.addConcept(code);
			}
			ValueSetComposeComponent comp = new ValueSetComposeComponent();
		
			comp.addInclude(concepts);
			
			valueSet.setCompose(comp);
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(valueSet);
			exchange.getIn().setHeader(Exchange.HTTP_PATH,"/ValueSet/"+valueSet.getId());
			exchange.getIn().setBody(Response);
		}
		else
		{
			CodeSystem codeSystem = new CodeSystem();
			
			//ConceptSetComponent concepts = new ConceptSetComponent();
			//concepts.setSystem(system);
			
			Extension extension = new Extension();
			extension = new Extension();
			extension.setUrl("http://west.riding.nhs.uk/fhir/StructureDefinition/vocabulary-filename");
			extension.setValue(new StringType(exchange.getIn().getHeader(Exchange.FILE_NAME).toString()));
			codeSystem.addExtension(extension);
			
			codeSystem.setId(idStr);
			
			Identifier identifier = new Identifier();
			identifier.setSystem("urn:ietf:rfc:3986").setValue(vocab.getId());
			codeSystem.setIdentifier(identifier);
			
			codeSystem.setUrl("http://west.riding.nhs.uk/fhir/"+idStr);
			codeSystem.setName(vocab.name);
			
			switch(vocab.getStatus())
			{
				case "active" :
				case "Active" :
				case "created" :
					codeSystem.setStatus(ConformanceResourceStatus.ACTIVE);
					break;
				case "superseded" :
					codeSystem.setStatus(ConformanceResourceStatus.RETIRED);
					break;
				default:
					codeSystem.setStatus(ConformanceResourceStatus.NULL);
			}
			
			for (int f=0;f<vocab.getConcept().size();f++)
			{
				ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
				
				concept.setCode(vocab.getConcept().get(f).getCode().toString());
				for (int g=0;g<vocab.getConcept().get(f).getDisplayName().size();g++)
				{
					if (vocab.getConcept().get(f).getDisplayName().get(g).getType() != null)
					{
						if (vocab.getConcept().get(f).getDisplayName().get(g).getType().equals("PT"))
						{
							concept.setDisplay(vocab.getConcept().get(f).getDisplayName().get(g).getValue());
						}
					}
					else
					{
						concept.setDisplay(vocab.getConcept().get(f).getDisplayName().get(g).getValue());
					}
				}
				codeSystem.addConcept(concept);
			}
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(codeSystem);
			exchange.getIn().setHeader(Exchange.HTTP_PATH,"/CodeSystem/"+codeSystem.getId());
			exchange.getIn().setBody(Response);
			
		}
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
	}

}
