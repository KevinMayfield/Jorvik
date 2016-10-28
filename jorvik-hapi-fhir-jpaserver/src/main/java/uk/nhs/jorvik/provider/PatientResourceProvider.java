package uk.nhs.jorvik.provider;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
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


public class PatientResourceProvider extends BaseJPAResourceProvider<Patient> implements IResourceProvider {

		
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
			startRequest(theRequest);
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.info("Calling patientDAO.create");
			return patientDAO.create(thePatient);  //, theRequestDetails
					
		}
		finally
		{
			endRequest(theRequest);
			log.info("Finished call createPatient");
		}
		
	}
	
	 @Read()
	    public MethodOutcome getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 	myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.info("session 2nd Attempt - Patient Read");
			}
			startRequest(theRequest);
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.info("Calling patientDAO.create");
			return patientDAO.read(theId);
			/*
	        Patient patient = new Patient();
	        patient.addIdentifier();
	        patient.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
	        patient.getIdentifier().get(0).setValue("00002");
	        patient.addName().addFamily("Test");
	        patient.getName().get(0).addGiven("PatientOne");
	        patient.setGender(AdministrativeGender.FEMALE);
	        return patient;*/
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
