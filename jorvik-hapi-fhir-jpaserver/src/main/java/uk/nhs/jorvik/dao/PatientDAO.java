package uk.nhs.jorvik.dao;



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

@Repository
@Transactional
public class PatientDAO extends BaseDAO<Patient>
implements IPatientDAO {
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.jorvik.provider.PatientResourceProvider.class);
	
		
	public PatientDAO() {
	    super(Patient.class);
	}
	
	public PatientDAO(SessionFactory sessionFactory) {
		super(Patient.class);
		this.sessionFactory = sessionFactory;
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
	public MethodOutcome create(Patient thePatient) {
		log.info("called create");
		PatientEntity entityPatient = new PatientEntity();
		entityPatient.setFamilyName(thePatient.getName().get(0).getFamilyAsSingleString());
		entityPatient.setGivenName(thePatient.getName().get(0).getGivenAsSingleString());
		try
		{
			Session session = getSessionFactory().openSession();
			session.persist(entityPatient);
			session.close();
			
			//emf.createEntityManager().persist(entityPatient);
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
		}
		finally
		{
			log.info("In the finally");
		}
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(thePatient);
		return method;
	}
	@Override
	public MethodOutcome read(IdType theId) {
		log.info("called create "+ theId.toString());
		Patient patient = null;
		try
		{
			Session session = getSessionFactory().openSession();
			PatientEntity entityPatient = (PatientEntity)session.get(PatientEntity.class,Integer.parseInt(theId.toString()));
			session.close();
			patient = new Patient();
			patient.addIdentifier();
	        patient.getIdentifier().get(0).setSystem(new String("urn:hapitest:mrns"));
	        patient.getIdentifier().get(0).setValue("00002");
	        patient.addName().addFamily(entityPatient.getFamilyName());
	        patient.getName().get(0).addGiven(entityPatient.getGivenName());
	        patient.setGender(AdministrativeGender.FEMALE);
	        
	        MethodOutcome method = new MethodOutcome();
			method.setResource(patient);
			return method;
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
			log.error(ex.getStackTrace().toString());
		}
		finally
		{
			log.info("In the finally");
		}
		
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(patient);
		return method;
	}

	

	

	
}