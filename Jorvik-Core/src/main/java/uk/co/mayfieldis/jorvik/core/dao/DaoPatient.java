package uk.co.mayfieldis.jorvik.core.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;

import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;


public class DaoPatient {
	
	private String NHS_Number;
	
	private String PrimaryPatient_Number;
	
	private String SecondaryPatient_Number;
	
	private String IPRN;
	
	private String Sex;
	
	private String Surname;
	
	private String Forename;
	
	private String Date_of_Birth;
	
	private String Address_Line_1;
	
	private String Address_Line_2;
	
	private String Address_Line_3;
	
	private String Address_Line_4;
	
	private String Post_Code;
	
	private String GPGMPCode;
	private String GPLocalCode;
	private String GPSurname;
	private String GPInitials;
	
	private String PracticeODSCode;
	private String PracticeName;
	private String PracticeAd1;
	private String PracticeAd2;
	private String PracticeAd3;
	private String PracticePostCode;
	
	public void setNHSNumber(String NHSNumber)
	{
		this.NHS_Number = NHSNumber;
	}
	public void setSecondaryPatient_Number(String SecondaryPatient_Number)
	{
		this.SecondaryPatient_Number = SecondaryPatient_Number;
	}
	public void setPrimaryPatient_Number(String PrimaryPatient_Number)
	{
		this.PrimaryPatient_Number = PrimaryPatient_Number;
	}
	public void setIPRN(String IPRN)
	{
		this.IPRN = IPRN;
	}

	public void setSex(String Sex)
	{
		this.Sex = Sex;
	}

	public void setSurname(String Surname)
	{
		this.Surname = Surname;
	}
	
	public void setForename(String Forename)
	{
		this.Forename = Forename;
	}
	
	public void setDate_of_Birth(String Date_of_Birth)
	{
		this.Date_of_Birth = Date_of_Birth;
	}
	
	public void setAddress_Line_1(String Address_Line_1)
	{
		this.Address_Line_1 = Address_Line_1;
	}
	
	public void setAddress_Line_2(String Address_Line_2)
	{
		this.Address_Line_2 = Address_Line_2 ;
	}
	
	public void setAddress_Line_3(String Address_Line_3)
	{
		this.Address_Line_3 = Address_Line_3;
	}
	
	public void setAddress_Line_4(String Address_Line_4)
	{
		this.Address_Line_4 = Address_Line_4;
	}
	
	public void setPost_Code(String Post_Code)
	{
		this.Post_Code = Post_Code;
	}
	
	public String getNHSNumber()
	{
		return NHS_Number;
	}
	
	public String getSecondaryPatient_Number()
	{
		return SecondaryPatient_Number;
	}
	public String getPrimaryPatient_Number()
	{
		return PrimaryPatient_Number;
	}
	
	public String getIPRN()
	{
		return IPRN;
	}
	
	public String getSex()
	{
		return Sex;
	}
	
	public String getSurname()
	{
		return Surname;
	}
	
	public String getForename()
	{
		return  Forename;
	}
	
	public String getDate_of_Birth()
	{
		return  Date_of_Birth;
	}
	
	public String getAddress_Line_1()
	{
		return  Address_Line_1;
	}
	
	public String getAddress_Line_2()
	{
		return  Address_Line_2 ;
	}
	
	public String getAddress_Line_3()
	{
		return  Address_Line_3;
	}
	
	public String getAddress_Line_4()
	{
		return  Address_Line_4;
	}
	
	public String getPost_Code()
	{
		return Post_Code;
	}
	
	public void setGPGMPCode(String GPGMPCode)
	{
		this.GPGMPCode = GPGMPCode;
	}
	public void setGPLocalCode(String GPLocalCode)
	{
		this.GPLocalCode = GPLocalCode;
	}
	public void setGPSurname(String GPSurname)
	{
		this.GPSurname = GPSurname;
	}
	public void setGPInitials(String GPInitials)
	{
		this.GPInitials = GPInitials;
	}
	public void setPracticeODSCode(String PracticeODSCode)
	{
		this.PracticeODSCode = PracticeODSCode;
	}
	public void setPracticeName(String PracticeName)
	{
		this.PracticeName = PracticeName;
	}
	public void setPracticeAd1(String PracticeAd1)
	{
		this.PracticeAd1 = PracticeAd1;
	}
	public void setPracticeAd2(String PracticeAd2)
	{
		this.PracticeAd2 = PracticeAd2;
	}
	public void setPracticeAd3(String PracticeAd3)
	{
		this.PracticeAd3 = PracticeAd3;
	}
	public void setPracticePostCode(String PracticePostCode)
	{
		this.PracticePostCode = PracticePostCode;
	}
	
	public String getGPGMPCode()
	{
		return GPGMPCode;
	}
	public String getGPLocalCode()
	{
		return GPLocalCode;
	}
	public String getGPSurname()
	{
		return GPSurname;
	}
	public String getGPInitials()
	{
		return GPInitials;
	}
	public String getPracticeODSCode()
	{
		return PracticeODSCode;
	}
	public String getPracticeName()
	{
		return PracticeName;
	}
	public String getPracticeAd1()
	{
		return PracticeAd1;
	}
	public String getPracticeAd2()
	{
		return PracticeAd2;
	}
	public String getPracticeAd3()
	{
		return PracticeAd3;
	}
	public String getPracticePostCode()
	{
		return PracticePostCode;
	}
	
	
	public Patient ConverttoFHIRPatient(NHSTrustFHIRCodeSystems codeSystem)
	{
		DaoPatient patientrs =this;
		Patient patient = new Patient();
		
		patient.setId(patientrs.getIPRN());
		
		// This is different to GPSoc requirement.
		if (patientrs.getNHSNumber() !=null && !patientrs.getNHSNumber().isEmpty())
		{
			patient.addIdentifier()
        		.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND)
        		.setValue(patientrs.getNHSNumber())
        		.setUse(IdentifierUse.OFFICIAL);
		}
		if (patientrs.getPrimaryPatient_Number() != null && !patientrs.getPrimaryPatient_Number().isEmpty())
		{
			patient.addIdentifier()
        		.setSystem(codeSystem.getURI_PATIENT_PRIMARY_IDENTIFIER())
        		.setValue(patientrs.getPrimaryPatient_Number())
        		.setUse(IdentifierUse.USUAL);
		}
		if (patientrs.getIPRN() != null && !patientrs.getIPRN().isEmpty())
		{
			patient.addIdentifier()
        		.setSystem(codeSystem.getURI_PATIENT_SECONDARY_IDENTIFIER())
        		.setValue(patientrs.getIPRN())
        		.setUse(IdentifierUse.SECONDARY);
		}
		/*
		if (patientrs.getUN1() != null && !patientrs.getUN1().isEmpty())
		{
			patient.addIdentifier()
        		.setSystem(CHFTFHIRCodeSystems.URI_PATIENT_UN1)
        		.setValue(patientrs.getUN1())
        		.setUse(IdentifierUse.SECONDARY);
		}
		*/
		if (patientrs.getDate_of_Birth() != null && !patientrs.getDate_of_Birth().isEmpty())
		{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			
        	try {
        		Date dob;
        		dob = fmt.parse(patientrs.getDate_of_Birth());
        		patient.setBirthDate(dob);
        	} catch (ParseException e1) {
        	// TODO Auto-generated catch block
        	}
		}
        patient.addName()
    		.addFamily(patientrs.getSurname())
    		.addGiven(patientrs.getForename());
        
        if (patientrs.getAddress_Line_1() != null && !patientrs.getAddress_Line_1().isEmpty())
        {
        	patient.addAddress()
        		.addLine(patientrs.getAddress_Line_1())
        		.addLine(patientrs.getAddress_Line_2())
        		.setCity(patientrs.getAddress_Line_3())
        		.setState(patientrs.getAddress_Line_4())
        		.setPostalCode(patientrs.getPost_Code());
        }
        if (patientrs.getSex() != null && !patientrs.getSex().isEmpty())
        {
        	switch (patientrs.getSex())
        	{
        		case "M":  
        			patient.setGender(AdministrativeGender.MALE);
        			break;
        		case "F":  
        			patient.setGender(AdministrativeGender.FEMALE);
        			break;
        		default:
        			patient.setGender(AdministrativeGender.NULL);
        			break;
        	}
        }
        if (patientrs.getGPGMPCode() !=null && !patientrs.getGPGMPCode().isEmpty())
        {
        	Practitioner GP = new Practitioner();
        	GP.setId("#gp");
        	GP.addIdentifier()
        		.setSystem(FHIRCodeSystems.URI_NHS_GMP_CODE)
        		.setValue(getGPGMPCode());
        	if (patientrs.getGPLocalCode() != null && !patientrs.getGPLocalCode().isEmpty())
        	{
        		GP.addIdentifier()
				.setSystem(codeSystem.getURI_NHSOrg_PAS_CONSULTANT_CODE())
				.setValue(patientrs.getGPLocalCode());
        	}
        	if (patientrs.getGPSurname() !=null && !patientrs.getGPSurname().isEmpty())
        	{
        		HumanName name = new HumanName();
				name.addFamily(patientrs.getGPSurname());
				name.addGiven(patientrs.getGPInitials());
				GP.addName(name);
			}
        	patient.getContained().add(GP);
        	Reference ref = new Reference();
			ref.setReference("#gp");
			patient.addGeneralPractitioner(ref);
        }
        if (patientrs.getPracticeODSCode() !=null && !patientrs.getPracticeODSCode().isEmpty())
        {
        	Organization practice = new Organization();
        	practice.setId("#prac");
        	practice.setName(patientrs.getPracticeName());
        	practice.addIdentifier()
        		.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE)
        		.setValue(patientrs.getPracticeODSCode());
        	
        	if (patientrs.getPracticeAd1() != null && !patientrs.getPracticeAd1().isEmpty())
            {
            	practice.addAddress()
            		.addLine(patientrs.getPracticeAd1())
            		.addLine(patientrs.getPracticeAd2())
            		.setCity(patientrs.getPracticeAd3())
            		.setPostalCode(patientrs.getPracticePostCode());
            }
        	
        	patient.getContained().add(practice);
        	Reference ref = new Reference();
			ref.setReference("#prac");
			patient.setManagingOrganization(ref);
        }
		return patient;
				
	}
}
