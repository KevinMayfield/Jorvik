package uk.nhs.riding.west.fhir.vocabularies;

import java.io.ByteArrayInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.math.NumberUtils;
import org.hl7.fhir.dstu3.model.Enumerations.ConformanceResourceStatus;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ValueSetComposeComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.UriDt;

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
		
		ValueSet valueSet = new ValueSet();
		ConceptSetComponent concepts = new ConceptSetComponent();
		
		Extension extension = new Extension();
		extension.setUrl("http://hl7.org/fhir/StructureDefinition/valueset-oid");
		extension.setValue(new UriType("urn:oid:"+vocab.getId()));
		valueSet.addExtension(extension);
		
		extension = new Extension();
		extension.setUrl("http://west.riding.nhs.uk/fhir/StructureDefinition/vocabulary-filename");
		extension.setValue(new StringType(exchange.getIn().getHeader(Exchange.FILE_NAME).toString()));
		valueSet.addExtension(extension);
		
		// Stop http FHIR queries from processing input parameters
		//exchange.getIn().removeHeaders("*");
		String vocabName = vocab.getName();
		exchange.getIn().setHeader("ActiveStatus", vocab.getStatus());
		switch(vocab.getStatus())
		{
			case "active" :
			case "Active" :
			case "created" :
				valueSet.setStatus(ConformanceResourceStatus.ACTIVE);
				exchange.getIn().setHeader("ActiveStatus", "active" );
				break;
			case "superseded" :
				valueSet.setStatus(ConformanceResourceStatus.RETIRED);
				break;
			default:
				valueSet.setStatus(ConformanceResourceStatus.NULL);
		}
		
		
		if (vocab.getId().contains("."))
		{
			concepts.setSystem("urn:oid:"+vocab.getId());
			
			if (vocab.getId().contains("2.16.840.1.113883.2.1.9") || (vocab.getId().contains("2.16.840.1.113883.12.")))
			{
				vocabName = "NHSITK-v2-"+vocabName;
			}
			else if (vocab.getId().contains("2.16.840.1.113883.2.1."))
			{
				if (vocab.getId().equals("2.16.840.1.113883.2.1.3.2.4.15"))
				{
					concepts.setSystem("http://snomed.info/sct");
				}
				vocabName = "NHSITK-v3-"+vocabName;
				
			}
			else
			{
				vocabName = "NotUK-"+vocabName;
				exchange.getIn().setHeader("ActiveStatus", "NotUK" );
			}
		}
		else if (NumberUtils.isNumber(vocab.getId())) 
		{
			concepts.setSystem("http://snomed.info/sct");
			vocabName = "NHSITK-SCT-"+vocabName;
		}
		else
		{
			// May not be robust
			concepts.setSystem("http://snomed.info/sct");
		}
		exchange.getIn().setHeader(Exchange.FILE_NAME, vocabName+".XML");
		
		String idStr = vocabName.replace(" ","-").toLowerCase();
		valueSet.setId(idStr.substring(0, idStr.length()));
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
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		exchange.getIn().setBody(Response);
				
	}

}
