package uk.nhs.jorvik.provider;

import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.DocumentReference;


import org.springframework.stereotype.Component;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.jorvik.dao.IDocumentReferenceDAO;

@Component
public class DocumentReferenceResourceProvider extends BaseJPAResourceProvider<DocumentReference> implements IResourceProvider {

	@Override
	public Class<DocumentReference> getResourceType() {
		// TODO Auto-generated method stub
		return DocumentReference.class;
	}
	
	 @Read()
	    public DocumentReference getResourceById(@IdParam IdType theId) {
		 	DocumentReference documentReference = new DocumentReference();
		 	documentReference.addIdentifier();
		 	documentReference.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
		 	documentReference.getIdentifier().get(0).setValue("00002");
	        
	        
	        return documentReference;
	    }
	 @Search()
	    public List<DocumentReference> getPatient(@OptionalParam(name = DocumentReference.SP_TYPE) StringParam theType, @OptionalParam(name=DocumentReference.SP_CLASS) StringParam theClass) {
		 	DocumentReference documentReference = new DocumentReference();
		 	documentReference.setId("1");
		 	documentReference.addIdentifier();
		 	documentReference.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
		 	documentReference.getIdentifier().get(0).setValue("00002");
	    
	        return Collections.singletonList(documentReference);
	    }
}
