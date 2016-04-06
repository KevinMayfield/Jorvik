package uk.co.mayfieldis.dao;



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
	
	
}
