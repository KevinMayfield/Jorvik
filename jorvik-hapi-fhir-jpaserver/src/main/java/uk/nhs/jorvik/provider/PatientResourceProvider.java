package uk.nhs.jorvik.provider;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.hl7.fhir.dstu3.model.Patient;
import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.IdType;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.jorvik.dao.PatientDAO;



public class PatientResourceProvider extends BaseProvider implements IResourceProvider {

		
	@Override
	public Class<Patient> getResourceType() {
		// TODO Auto-generated method stub
		return Patient.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	

	private static final Logger log = LoggerFactory.getLogger(uk.nhs.jorvik.provider.PatientResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createPatient(HttpServletRequest theRequest,@ResourceParam Patient thePatient) {
		
		log.info("Called createPatient");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		Patient theNewPatient = null;
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
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.info("Calling patientDAO.create");
			theNewPatient = patientDAO.create(thePatient);
			log.info("Return the New Patient id = "+thePatient.getId());
			method.setId(thePatient.getIdElement());
			method.setResource(theNewPatient);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.info("Finished call createPatient");
		}
		return method;  //, theRequestDetails
	}
	
	 @Read()
	    public Patient getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 	myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.info("session 2nd Attempt - Patient Read");
			}
			startRequest(theRequest);
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.info("Calling patientDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(patientDAO.read(theId));
			return (Patient) method.getResource();   //, theRequestDetails
			
			
	    }
	 @Search()
	    public List<Patient> getPatient(@OptionalParam(name = Patient.SP_FAMILY) StringParam theFamilyName, @OptionalParam(name=Patient.SP_GIVEN) StringParam theGivenName) {
	        Patient patient = new Patient();
	        patient.setId("1");
	        patient.addIdentifier();
	        patient.getIdentifier().get(0).setUse(IdentifierUse.OFFICIAL);
	        patient.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
	        patient.getIdentifier().get(0).setValue("00001");
	        patient.addName();
	        if (theFamilyName != null)
	        {
	        	patient.getName().get(0).addFamily(theFamilyName.getValue());
	        }
	        else
	        {
	        	patient.getName().get(0).addFamily("Smith");
	        }
	        
	 		if (theGivenName != null)
	 		{
	 			patient.getName().get(0).addGiven(theGivenName.getValue());
	 		}
	 		else
	 		{
	 			patient.getName().get(0).addGiven("Eric");
	 		}
	        patient.setGender(AdministrativeGender.MALE);
	        return Collections.singletonList(patient);
	    }
}
