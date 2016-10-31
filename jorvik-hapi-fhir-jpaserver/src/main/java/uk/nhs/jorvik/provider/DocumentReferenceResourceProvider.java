package uk.nhs.jorvik.provider;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.DocumentReference;


import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.jorvik.dao.DocumentReferenceDAO;
import uk.nhs.jorvik.dao.PatientDAO;



public class DocumentReferenceResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<DocumentReference> getResourceType() {
		// TODO Auto-generated method stub
		return DocumentReference.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.jorvik.provider.PatientResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createDocumentReference(HttpServletRequest theRequest,@ResourceParam DocumentReference theDocRef) {
		
		log.info("Called createDocumentReference");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		DocumentReference theNewDocRef = null;
		myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		
		if (sessionFactory != null)
		{
			log.info("session");
		}
		else
		{
			log.info("session NULL");
			sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.info("session 2nd Attempt - Patient Create");
			}
		}
		
		try 
		{
			//startRequest(theRequest);
			DocumentReferenceDAO docRefDAO = new DocumentReferenceDAO(sessionFactory); 
			log.info("Calling documentReferenceDAO.create");
			theNewDocRef = docRefDAO.create(theDocRef);
			log.info("Return the New DocumentReference id = "+theDocRef.getId());
			method.setId(theDocRef.getIdElement());
			method.setResource(theNewDocRef);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.info("Finished call createDocumentReference");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public DocumentReference getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.info("session 2nd Attempt - DocumentReference Read");
			}
			startRequest(theRequest);
			DocumentReferenceDAO docRefDAO = new DocumentReferenceDAO(sessionFactory); 
			log.info("Calling documentReferenceDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(docRefDAO.read(theId));
			return (DocumentReference) method.getResource();   //, theRequestDetails
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
