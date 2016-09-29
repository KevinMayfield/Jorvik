package uk.co.mayfieldis.jorvik.core.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationStatus;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;

import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;


public class DaoEncounter {
	
	private DaoPatient patient;
	
	private String encounterId;
	
	private String arrivalStartDate;
	
	private String treatmentStartDate;
	
	private String dischargeDate;
	
	private DaoEncounterStatus status;
	
	private DaoEncounterClass eClass;
	
	private String type; // SNOMED!
	
	private String reasonCode;
	
	private String reasonDesc;
	
	private String reasonCodeSystem;
	
	private String consultant;
	
	private String consultantName;
	
	private String consultantPAS;
	
	private String ward;
	
	private String hospital;
	
	private String specialty;
	
	private String specialtyName;

	public void setconsultantPAS(String consultantPAS)
	{
		this.consultantPAS = consultantPAS;
	}
		
	public String getconsultantPAS()
	{
		return consultantPAS;
	}
	
	public void setconsultantName(String consultantName)
	{
		this.consultantName = consultantName;
	}
		
	public String getconsultantName()
	{
		return consultantName;
	}
	
	public void setconsultant(String consultant)
	{
		this.consultant = consultant;
	}
		
	public String getconsultant()
	{
		return consultant;
	}
	
	public void setWard(String ward)
	{
		this.ward = ward;
	}
		
	public String getWard()
	{
		return ward;
	}
	
	public void setHospital(String hospital)
	{
		this.hospital = hospital;
	}
		
	public String getsHospital()
	{
		return hospital;
	}

	
	public void setspecialty(String specialty)
	{
		this.specialty = specialty;
	}
		
	public String getspecialty()
	{
		return specialty;
	}

	
	public void setspecialtyName(String specialtyName)
	{
		this.specialtyName = specialtyName;
	}
		
	public String getspecialtyName()
	{
		return specialtyName;
	}
	
	public void setPatient(DaoPatient patient)
	{
		this.patient = patient;
	}
		
	public DaoPatient getPatient()
	{
		return patient;
	}
		
	public void setEncounterId(String encounterId)
	{
		this.encounterId = encounterId;
	}
	
	public String getEncounterId()
	{
		return encounterId;
	}
	
	public void setArrivalStartDate(String arrivalStartDate)
	{
		this.arrivalStartDate = arrivalStartDate;
	}
	
	public String getArrivalStartDate()
	{
		return arrivalStartDate;
	}
	
	public void setDischargeDate(String dischargeDate)
	{
		this.dischargeDate = dischargeDate;
	}
	
	public String getDischargeDate()
	{
		return dischargeDate;
	}
	
	public void setTreatmentStartDate(String treatmentStartDate)
	{
		this.treatmentStartDate = treatmentStartDate;
	}
	
	public String getTreatmentStartDate()
	{
		return treatmentStartDate;
	}
	
	public void setStatus(DaoEncounterStatus status)
	{
		this.status = status;
	}
	
	public DaoEncounterStatus getStatus()
	{
		return status;
	}
	
	public void setEClass(DaoEncounterClass eClass)
	{
		this.eClass = eClass;
	}
	
	public DaoEncounterClass getEClass()
	{
		return eClass;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}

	public void setReasonCode(String reasonCode)
	{
		this.reasonCode = reasonCode;
	}
	
	public String getReasonCode()
	{
		return reasonCode;
	}
	
	public void setReasonDesc(String reasonDesc)
	{
		this.reasonDesc = reasonDesc;
	}
	
	public String getReasonDesc()
	{
		return reasonDesc;
	}
	
	public void setReasonCodeSystem(String reasonCodeSystem)
	{
		this.reasonCodeSystem = reasonCodeSystem;
	}
	
	public String getReasonCodeSystem()
	{
		return reasonCodeSystem;
	}
	
	public Encounter ConverttoFHIREncounter(NHSTrustFHIRCodeSystems codeSystem)
	{
		DaoEncounter encounter = this;
		
		Encounter encounterFHIR = new Encounter();
		
		encounterFHIR.setId(encounter.getEncounterId());
		encounterFHIR.addIdentifier()
			.setSystem(codeSystem.geturiNHSOrgActivityId())
			.setValue(encounter.getEncounterId());
		
		if (encounter.getEClass()==DaoEncounterClass.EMERGENCY) 
		{
			encounterFHIR.setClass_(new Coding().setCode("EMERGENCY"));
		}
		else if (encounter.getEClass()==DaoEncounterClass.INPATIENT) 
		{
			if (encounter.getEncounterId().contains("-"))
			{
				
				encounterFHIR.setClass_(new Coding().setCode("OUTPATIENT"));
			}
			else
			{
				encounterFHIR.setClass_(new Coding().setCode("INPATIENT"));
			}
		} 
		
		if (encounter.getReasonCode()!=null)
		{
			encounterFHIR.addReason()
			.addCoding()
				.setCode(encounter.getReasonCode())
				.setDisplay(encounter.getReasonDesc())
				.setSystem(encounter.getReasonCodeSystem());
		}
		encounterFHIR.setStatus(EncounterStatus.ARRIVED);
		
		if ((encounter.getTreatmentStartDate() != null) && (!encounter.getTreatmentStartDate().isEmpty()))
		{
			encounterFHIR.setStatus(EncounterStatus.INPROGRESS);
		}
		if ((encounter.getArrivalStartDate() != null) && (!encounter.getArrivalStartDate().isEmpty()))
		{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Period period = new Period();
			
        	try {
        		
        		Date date;
        		date = fmt.parse(encounter.getArrivalStartDate());
        		period.setStart(date);
        		
        		
        	} catch (ParseException e1) {
        	// TODO Auto-generated catch block
        	}
        	if ((encounter.getDischargeDate() != null) && (!encounter.getDischargeDate().isEmpty()))
			{
				try {
					encounterFHIR.setStatus(EncounterStatus.FINISHED);
	        		Date date;
	        		date = fmt.parse(encounter.getDischargeDate());
	        		period.setEnd(date);
	        		
	        	} 
				catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
        	encounterFHIR.setPeriod(period);
		}
		if (encounterFHIR.getClass_().getCode() == "OUTPATIENT")
		{
			// Hardcoded to only return completed outpatient appointments
			encounterFHIR.setStatus(EncounterStatus.FINISHED);
		}
		if (encounter.getPatient() != null)
		{
			Patient patientFHIR = new Patient();
			
			if (encounter.getPatient().getPrimaryPatient_Number()!=null)
			{
				patientFHIR.addIdentifier()
					.setValue(encounter.getPatient().getPrimaryPatient_Number())
					.setSystem(codeSystem.getURI_PATIENT_PRIMARY_IDENTIFIER());
			}
			if (encounter.getPatient().getNHSNumber()!=null)
			{
				patientFHIR.addIdentifier()
					.setValue(encounter.getPatient().getNHSNumber())
					.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND);
			}
			if (encounter.getPatient().getSecondaryPatient_Number()!=null)
			{
				patientFHIR.addIdentifier()
					.setValue(encounter.getPatient().getPrimaryPatient_Number())
					.setSystem(codeSystem.getURI_PATIENT_SECONDARY_IDENTIFIER());
			}
			/*
			if (encounter.getPatient().getUN1()!=null)
			{
				patientFHIR.addIdentifier()
					.setValue(encounter.getPatient().getUN1())
					.setSystem(codeSystem.getURI_PATIENT_UN1());
			}
			*/
			 if (encounter.getPatient().getSex() != null && !encounter.getPatient().getSex().isEmpty())
		        {
		        	switch (encounter.getPatient().getSex())
		        	{
		        		case "M":  
		        			patientFHIR.setGender(AdministrativeGender.MALE);
		        			break;
		        		case "F":  
		        			patientFHIR.setGender(AdministrativeGender.FEMALE);
		        			break;
		        		default:
		        			patientFHIR.setGender(AdministrativeGender.NULL);
		        			break;
		        	}
		        }
			 if (encounter.getPatient().getDate_of_Birth() != null && !encounter.getPatient().getDate_of_Birth().isEmpty())
				{
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					
		        	try {
		        		Date dob;
		        		dob = fmt.parse(encounter.getPatient().getDate_of_Birth());
		        		patientFHIR.setBirthDate(dob);
		        	} catch (ParseException e1) {
		        	// TODO Auto-generated catch block
		        	}
				}
			 patientFHIR.setId("#pat");
			encounterFHIR.getContained().add(patientFHIR);
	
			encounterFHIR.addParticipant()
					.addType().addCoding()
						.setCode("CON")
						.setDisplay("consultant")
						.setSystem("http://hl7.org/fhir/v3/ParticipationType");
			
			Practitioner prac = new Practitioner();
			
			prac.addIdentifier()
				.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS)
				.setValue(encounter.getconsultant());
			
			prac.addIdentifier()
				.setSystem(codeSystem.getURI_NHSOrg_PAS_CONSULTANT_CODE())
				.setValue(encounter.getconsultantPAS());
			
			prac.setId("#prac");
			if (encounter.getconsultantName()!= null && !encounter.getconsultantName().isEmpty())
			{
				String[] str = encounter.getconsultantName().split(" ");
				
				HumanName name = new HumanName();
				if ((str.length>2)&&(str[2] != null))
				{
					name.addFamily(str[2]);
				}
				if ((str.length>1)&&(str[1] != null))
				{
					name.addGiven(str[1]);
				}
				if ((str.length>0)&&(str[0] != null))
				{
					name.addPrefix(str[0]);
				}
				prac.addName(name);
			}
			encounterFHIR.addType()
				.addCoding()
					.setCode(encounter.getspecialty())
					.setSystem(FHIRCodeSystems.URI_OID_NHS_SPECIALTIES)
					.setDisplay(encounter.getspecialtyName());
			
			encounterFHIR.getContained().add(prac);
			
			Reference ref = new Reference();
			ref.setReference("#prac");
			encounterFHIR.getParticipant().get(0)
				.setIndividual(ref);
			
			
			Location loc = new Location();
			loc.setId("#loc");
			
			loc.addIdentifier()
				.setValue(encounter.getWard())
				.setSystem(codeSystem.getURI_NHSOrg_WARD_CODE());
				
			loc.addIdentifier()
				.setValue(encounter.getsHospital())
				.setSystem(codeSystem.getURI_NHSOrg_HOSPITAL_CODE());
			
			encounterFHIR.getContained().add(loc);
			
			ref = new Reference();
			ref.setReference("#loc");
			encounterFHIR.addLocation()
				.setLocation(ref)
				.setStatus(EncounterLocationStatus.ACTIVE);
			

		}
		return encounterFHIR;
				
	}
}
