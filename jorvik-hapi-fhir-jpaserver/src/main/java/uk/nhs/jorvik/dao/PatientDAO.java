package uk.nhs.jorvik.dao;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import uk.nhs.jorvik.entity.PatientEntity;
import uk.nhs.jorvik.entity.PatientIdentifier;

@Repository
@Transactional
public class PatientDAO extends BaseDAO<Patient>
implements IPatientDAO {
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.jorvik.provider.PatientResourceProvider.class);
	
		
	public PatientDAO() {
	    super(Patient.class);
	}
	
	public PatientDAO(EntityManagerFactory entityManagerFactory) {
		super(Patient.class);
		this.emf = entityManagerFactory;
	}

	
	@Override
	public Class<Patient> getResourceType() {
		// TODO Auto-generated method stub
		return Patient.class;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, IIdType theId,
			RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Patient create(Patient thePatient) {
		log.info("called Patient create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager");
			em.getTransaction().begin();
			
			log.info("Call persist");
			PatientEntity ep = new PatientEntity();
			ep.setFamilyName(thePatient.getName().get(0).getFamilyAsSingleString());
			ep.setGivenName(thePatient.getName().get(0).getGivenAsSingleString());
			ep.setGender(thePatient.getGender().toCode() );
			log.info("Built Patient entity");
			
			em.persist(ep);
		
			List<PatientIdentifier> pids = new ArrayList<PatientIdentifier>();
			PatientIdentifier pi = new PatientIdentifier(ep);
			pi.setSystem(thePatient.getIdentifier().get(0).getSystem());
			pi.setValue(thePatient.getIdentifier().get(0).getValue());
			ep.setIdentifiers(pids);
			    
			em.persist(pi);
			
			em.getTransaction().commit();
			
			log.info("Called it PERSIST id="+ep.getId().toString());
			thePatient.setId(ep.getId().toString());
			
			em.close();
			
			log.debug("Finished call to persist Patient");
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
		}
		
		log.info("In the finally");
		return thePatient;		
	}
	@Override
	public Patient read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		Patient patient = null;
		try
		{
		
			EntityManager em = emf.createEntityManager();
				
			PatientEntity entityPatient = (PatientEntity) em.find(PatientEntity.class,Integer.parseInt(theId.getIdPart()));
			em.close();
			patient = new Patient();
			patient.addIdentifier();
	        patient.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
	        patient.getIdentifier().get(0).setValue("00002");
	        patient.addName().addFamily(entityPatient.getFamilyName());
	        patient.getName().get(0).addGiven(entityPatient.getGivenName());
	        patient.setGender(AdministrativeGender.FEMALE);
	        log.info("Built the PATIENT");
	       
			return patient;
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
			//log.error(ex.getStackTrace().toString());
		}
		finally
		{
			log.info("In the finally");
		}
		
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(patient);
		return patient;
	}

	

	

	
}