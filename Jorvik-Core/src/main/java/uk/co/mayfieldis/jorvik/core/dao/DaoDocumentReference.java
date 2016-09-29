package uk.co.mayfieldis.jorvik.core.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;

import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;


public class DaoDocumentReference {
	
	private String FileName;

	private String Author;

	private String ConsCode;

	private String ClinicCode;

	private String ClinicDate;

	private String PatientPrimaryIdentifierValue;

	private String NHSNumber;
	
	private String ID;

	private String Created;

	private String LastEditedBy;

	private String Department;

	private String SentStatus;

	private String ActivityID;

	private String LetterArea;

	private String ConsultantFamily;

	private String ConsultantGiven;

	private String ConsultantPrefix;

	private String ConsultantNationalCode;

	private String UserFamily;

	private String UserGiven;

	private String UserType;

	private String LastEdited;

	private String IsCopy;

	private String DepartmentCode;
	
	private String EncounterPeriodStart;
	
	private String EncounterPeriodEnd;
	
	private String Encounter_Class;

	private String Title;
	
	public void setTitle(String Title){
		this.Title= Title;
	}
	
	public String getTitle()
	{
		return Title;
	}
	
	public String getEncounterPeriodStart()
	{
		return EncounterPeriodStart;
	}
	
	public String getEncounterPeriodEnd()
	{
		return EncounterPeriodEnd;
	}
	
	public String getEncounter_Class()
	{
		return Encounter_Class;
	}
	public void setEncounterPeriodStart(String EncounterPeriodStart)
	{
		this.EncounterPeriodStart = EncounterPeriodStart;
	}
	
	public void setEncounterPeriodEnd(String EncounterPeriodEnd)
	{
		this.EncounterPeriodEnd= EncounterPeriodEnd;
	}
	
	public void setEncounter_Class(String Encounter_Class)
	{
		this.Encounter_Class = Encounter_Class;
	}
	public String getFileName()
	{
		return FileName;
	}

	public String getAuthor(){
		return Author;
	}

	public String getConsCode(){
		return ConsCode;
	}

	public String getClinicCode(){
		return ClinicCode;
	}

	public String getClinicDate(){
		return ClinicDate;
	}

	public String getPatientPrimaryIdentifierValue(){
		return PatientPrimaryIdentifierValue;
	}
	public String getNHSNumber(){
		return NHSNumber;
	}

	public String getID(){
		return ID;
	}

	public String getCreated(){
		return Created;
	}

	public String getLastEditedBy(){
		return LastEditedBy;
	}

	public String getDepartment(){
		return Department;
	}

	public String getSentStatus(){
		return SentStatus;
	}

	public String getActivityID(){
		return ActivityID;
	}

	public String getLetterArea(){
		return LetterArea;
	}

	public String getConsultantFamily(){
		return ConsultantFamily;
	}

	public String getConsultantGiven(){
		return ConsultantGiven;
	}

	public String getConsultantPrefix(){
		return ConsultantPrefix;
	}

	public String getConsultantNationalCode(){
		return ConsultantNationalCode;
	}

	public String getUserFamily(){
		return UserFamily;
	}

	public String getUserGiven(){
		return UserGiven;
	}

	public String getUserType(){
		return UserType;
	}

	public String getLastEdited(){
		return LastEdited;
	}

	public String getIsCopy(){
		return IsCopy;
	}
	
	public String getDepartmentCode(){
		return DepartmentCode;
	}
	
	public void setFileName(String FileName)
	{
		this.FileName= FileName;
	}

	public void setAuthor(String Author){
		this.Author= Author;
	}

	public void setConsCode(String ConsCode){
		this.ConsCode= ConsCode;
	}

	public void setClinicCode(String ClinicCode){
		this.ClinicCode= ClinicCode;
	}

	public void setClinicDate(String ClinicDate){
		this.ClinicDate= ClinicDate;
	}

	public void setPatientPrimaryIdentifierValue(String PatientPrimaryIdentifierValue){
		this.PatientPrimaryIdentifierValue= PatientPrimaryIdentifierValue;
	}
	
	public void setNHSNumber(String NHSNumber){
		this.NHSNumber = NHSNumber;
	}

	public void setID(String ID){
		this.ID= ID;
	}

	public void setCreated(String Created){
		this.Created= Created;
	}

	public void setLastEditedBy(String LastEditedBy){
		this.LastEditedBy= LastEditedBy;
	}

	public void setDepartment(String Department){
		this.Department= Department;
	}

	public void setSentStatus(String SentStatus){
		this.SentStatus= SentStatus;
	}

	public void setActivityID(String ActivityID){
		this.ActivityID= ActivityID;
	}

	public void setLetterArea(String LetterArea){
		this.LetterArea= LetterArea;
	}

	public void setConsultantFamily(String ConsultantFamily){
		this.ConsultantFamily = ConsultantFamily;
	}

	public void setConsultantGiven(String ConsultantGiven){
		this.ConsultantGiven= ConsultantGiven;
	}

	public void setConsultantPrefix(String ConsultantPrefix){
		this.ConsultantPrefix= ConsultantPrefix;
	}

	public void setConsultantNationalCode(String ConsultantNationalCode){
		this.ConsultantNationalCode= ConsultantNationalCode;
	}

	public void setUserFamily(String UserFamily){
		this.UserFamily= UserFamily;
	}

	public void setUserGiven(String UserGiven){
		this.UserGiven= UserGiven;
	}

	public void setUserType(String UserType){
		this.UserType= UserType;
	}

	public void setLastEdited(String LastEdited){
		this.LastEdited= LastEdited;
	}

	public void setIsCopy(String IsCopy){
		this.IsCopy= IsCopy;
	}
	
	public void setDepartmentCode(String DepartmentCode){
		this.DepartmentCode= DepartmentCode;
	}
	
	public String GetMimeType(String mime)
	{
		String mimeType = mime;
				switch (mime)
				{
					case "doc":
					case "DOC":
						mimeType ="application/msword";
						break;
					
					case "html":
					case "HTML":
						mimeType ="text/html";
						break;
					case "pdf":
					case "PDF":
						mimeType ="application/pdf";
						break;
					case "rtf":
					case "RTF":
						mimeType ="application/rtf";
						break;
					case "tif":
					case "TIF":
						mimeType ="image/tiff";
						break;
					case "zip":
					case "ZIP":
						mimeType ="application/zip";
						break;
					case "txt":
					case "TXT":
						mimeType = "text/plain";
						break;
				}
		return mimeType;
	}
	
	public DocumentReference ConverttoFHIRDocumentReference(String binaryLocation, NHSTrustFHIRCodeSystems codeSystem)
	{
		DaoDocumentReference docRef = this;
		
		DocumentReference docRefFHIR = new DocumentReference();
		
		docRefFHIR.setId(docRef.getID());
		Patient patient = new Patient();
		
		Encounter encounter = new Encounter();
		encounter.setId("#enc");
		Period encounterPeriod = new Period();
		
		
		Practitioner prac = new Practitioner();
		prac.setId("#prac");
		 
				
		Person person = new Person();
		person.setId("#pers");
		
		docRefFHIR.setStatus(DocumentReferenceStatus.CURRENT);
		
		Period period = new Period();
		CodeableConcept docClass = new CodeableConcept();
		CodeableConcept docType = new CodeableConcept();
		
		patient.addIdentifier()
			.setSystem(codeSystem.getURI_PATIENT_PRIMARY_IDENTIFIER())
			.setValue(docRef.getPatientPrimaryIdentifierValue());
		
		if (docRef.getNHSNumber() != null && !docRef.getNHSNumber().isEmpty())
		{
			patient.addIdentifier()
				.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND)
				.setValue(docRef.getNHSNumber());
		}

		if (docRef.getConsultantNationalCode()!=null && !docRef.getConsultantNationalCode().isEmpty())
		{
			prac.addIdentifier()
			.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS)
			.setValue(docRef.getConsultantNationalCode());
		}
		if (docRef.getConsCode()!=null && !docRef.getConsCode().isEmpty())
		{
			prac.addIdentifier()
			.setSystem(codeSystem.getURI_NHSOrg_PAS_CONSULTANT_CODE())
			.setValue(docRef.getConsCode());
		}
		
		if (docRef.getConsultantFamily()!=null && !docRef.getConsultantFamily().isEmpty())
		{
			HumanName consultantName = new HumanName();
			consultantName.addFamily(docRef.getConsultantFamily());
			if (docRef.getConsultantGiven()!=null && !docRef.getConsultantGiven().isEmpty())
			{
				consultantName.addGiven(docRef.getConsultantGiven());
			}
			if (docRef.getConsultantPrefix()!=null && !docRef.getConsultantPrefix().isEmpty())
			{
				consultantName.addPrefix(docRef.getConsultantPrefix());
			}
			prac.addName(consultantName);
		}
		
		if (docRef.getEncounter_Class()!=null && !docRef.getEncounter_Class().isEmpty())
		{
			switch (docRef.getEncounter_Class())
			{
				case "Inpatient":
					encounter.setClass_(new Coding().setCode("INPATIENT"));
					encounter.setStatus(EncounterStatus.FINISHED);
					break;
				case "Outpatient":
					encounter.setClass_(new Coding().setCode("OUTPATIENT"));
					encounter.setStatus(EncounterStatus.FINISHED);
					break;
				default: 
					encounter.setStatus(EncounterStatus.FINISHED);
					
			}
			docType.addCoding()
				.setSystem(FHIRCodeSystems.URI_SNOMED)
				.setCode("823701000000103")
				.setDisplay("Discharge letters");
			
			if (docRef.getActivityID()!=null && !docRef.getActivityID().isEmpty())
			{
				encounter.setId("#enc");
				encounter.addIdentifier()
					.setSystem(codeSystem.geturiNHSOrgActivityId())
					.setValue(docRef.getActivityID());
			}
			Attachment docAttachment = new Attachment();
			
			docAttachment.setContentType(GetMimeType("html"));
			docAttachment.setUrl(binaryLocation+"Binary/"+docRef.getFileName());
			docRefFHIR.addContent()
				.setAttachment(docAttachment);
			docRefFHIR.addIdentifier()
				.setSystem(codeSystem.geturiNHSOrgDischargeLetter())
				.setValue(docRef.getFileName());
		}
		else
		{
			switch (docRef.getLetterArea())
			{
				case "OP":
					docType.addCoding()
						.setSystem(FHIRCodeSystems.URI_SNOMED)
						.setCode("823681000000100")
						.setDisplay("Outpatient letter");
					encounter.setClass_(new Coding().setCode("OUTPATIENT"));
					encounter.setStatus(EncounterStatus.FINISHED);
					if (docRef.getActivityID()!=null && !docRef.getActivityID().isEmpty())
					{
						String activity = docRef.getActivityID().replace("|", ",");
						String[] activitySp = activity.split(",");
						if (activitySp.length>4)
						{
							//encounter.setId(activitySp[2]+"-"+activitySp[4]);
							encounter.setId("#enc");
							encounter.addIdentifier()
								.setSystem(codeSystem.geturiNHSOrgActivityId())
								.setValue(activitySp[2]+"-"+activitySp[4]);
						}
					}
					break;
				case "IP":
					docType.addCoding()
						.setSystem(FHIRCodeSystems.URI_SNOMED)
						.setCode("820221000000109")
						.setDisplay("Inpatient medical note");
					encounter.setClass_(new Coding().setCode("INPATIENT"));
					encounter.setStatus(EncounterStatus.FINISHED);
					if (docRef.getActivityID()!=null && !docRef.getActivityID().isEmpty())
					{
						String activity = docRef.getActivityID().replace("|", ",");
						String[] activitySp = activity.split(",");
						if (activitySp.length>2)
						{
							//encounter.setId(activitySp[2]);
							encounter.setId("#enc");
							encounter.addIdentifier()
								.setSystem(codeSystem.geturiNHSOrgActivityId())
								.setValue(activitySp[2]);
						}
					}
					break;
				default:
					docType.addCoding()
						.setSystem(FHIRCodeSystems.URI_SNOMED)
						.setCode("823691000000103")
						.setDisplay("Clinical letter");
			}
			Attachment docAttachment = new Attachment();
			
			docAttachment.setContentType(GetMimeType("doc"));
			docAttachment.setUrl(binaryLocation+"Binary/"+docRef.getFileName());
			docRefFHIR.addContent()
				.setAttachment(docAttachment);
			docRefFHIR.addIdentifier()
				.setSystem(codeSystem.geturiNHSOrgClinicalLetter())
				.setValue(docRef.getFileName());
			if (docRef.getTitle() != null && !docRef.getTitle().isEmpty())
		    {
		    	docRefFHIR.setDescription(docRef.getTitle());
		    }
		}
		if (docRef.getEncounterPeriodStart()!=null && !docRef.getEncounterPeriodStart().isEmpty())
		{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			
        	try {
        		Date date;
        		date = fmt.parse(docRef.getEncounterPeriodStart());
        		encounterPeriod.setStart(date);
        	} catch (ParseException e1) {
        	// TODO Auto-generated catch block
        	}
		}
		if (docRef.getEncounterPeriodEnd()!=null && !docRef.getEncounterPeriodEnd().isEmpty())
		{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			
        	try {
        		Date date;
        		date = fmt.parse(docRef.getEncounterPeriodEnd());
        		encounterPeriod.setEnd(date);
        	} catch (ParseException e1) {
        	// TODO Auto-generated catch block
        	}
		}
		
		if (docRef.getDepartmentCode()!=null && !docRef.getDepartmentCode().isEmpty())
		{	
			docClass.addCoding()
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES)
				.setCode(docRef.getDepartmentCode())
				.setDisplay(docRef.getDepartment());
		}
		// Changed from author to last editted by 
		if (docRef.getLastEditedBy()!=null && !docRef.getLastEditedBy().isEmpty() )
		{
			person.addIdentifier()
				.setSystem(codeSystem.geturiNHSOrgPASUSer())
				.setValue(docRef.getLastEditedBy());
				
		}
		Reference ref = new Reference();
		patient.setId("#pat");
        ref.setReference("#pat");
        docRefFHIR.setSubject(ref);
        docRefFHIR.getContained().add(patient);
		
        
        DocumentReference.DocumentReferenceContextComponent context = new DocumentReference.DocumentReferenceContextComponent();
        if (encounter !=null)
        {
        	encounter.setPeriod(encounterPeriod);
        	docRefFHIR. getContained().add(encounter);
        	Reference encRef = new Reference();
        	encRef.setReference("#enc");
        	context.setEncounter(encRef);
        }
        if (docRef.getCreated()!=null)
        {
        	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
        	try {
        		Date dt;
        		dt = fmt.parse(docRef.getCreated());
        		period.setStart(dt);
        		docRefFHIR.setCreated(dt);
        	} catch (ParseException e1) {
            	// TODO Auto-generated catch block
            }
        }
        	
        docRefFHIR.addAuthor().setReference("#prac");
        docRefFHIR.getContained().add(prac);
        
        docRefFHIR.setClass_(docClass);
		docRefFHIR.setType(docType);
		
		docRefFHIR.addAuthor().setReference("#pers");
	    docRefFHIR.getContained().add(person);
	    
	    if (docRef.getTitle() != null && !docRef.getTitle().isEmpty())
	    {
	    	docRefFHIR.setDescription(docRef.getTitle());
	    }
	        
	    context.setPeriod(period);
	   
	    docRefFHIR.setContext(context);
	     
		return docRefFHIR;
				
	}
	
}
