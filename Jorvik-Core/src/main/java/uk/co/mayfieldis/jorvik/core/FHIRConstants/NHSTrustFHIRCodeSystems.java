package uk.co.mayfieldis.jorvik.core.FHIRConstants;

import org.springframework.core.env.Environment;

// 2016-02-05 Added all constants from subprojects

//Added Gp and Practice  19/1/2016 
public class NHSTrustFHIRCodeSystems {
	
	 private String URI_PATIENT_HOSPITAL_NUMBER;
	 
	 private String URI_PATIENT_OTHER_NUMBER;
	 
	 private String URI_NHSOrg_PAS_CONSULTANT_CODE;
	 
	 private String URI_NHSOrg_WARD_CODE;
	 
	 private String URI_NHSOrg_HOSPITAL_CODE;
	 
	 private String uri_NHSOrg_EDMS_DocumentId;
	 
	 private String uri_NHSOrg_DocumentIndex;
	 
	 private String uri_NHSOrg_Kodak_DocumentId;
	 
	 private String uriNHSOrgActivityId;
	 
	 private String uriNHSOrgEDISActivityId;
	 
	 private String uriNHSOrgNorthgateDocumentId;
	 
	 private String URI_PATIENT_UN1;
	 
	 private String  uriNHSOrgPASUSer;
	 
	 private String  uriNHSOrgClinicalLetter;
	 
	 private String  uriNHSOrgDischargeLetter; 
	 
	 private String URI_NHSOrg_PAS_GP_CODE;
	 
	 private String uri_NHSOrg_EDMS_DrawerId;
	 
	 private String uri_NHSOrg_EDMS_DrawerId_Extension;
	 
	 private String URI_NHSOrg_EDIS_REASON_CODE_SYSTEM;
	 
	 private String URI_NHSOrg_SPECIALTY;
	 
	 private String URI_NHSOrg_CLINIC_CODE;
	 
	 private String URI_NHSOrg_REFERRAL_REASON;
	 
	 private String URI_NHSOrg_REFERRAL_REASON_ACCPETED_DATE;
	 
	 private String  uriNHSOrgLocation;
	 
	 private String uriNHSOrgAppointmentId;
	 
	 public Environment env;
	 
	 public void setValues(Environment env)
	 {
		 this.env=env;
		 
		 this.URI_PATIENT_HOSPITAL_NUMBER = env.getProperty("ORG.URIPATIENTHOSPITALNUMBER");
		 
		 this.URI_PATIENT_OTHER_NUMBER = env.getProperty("ORG.URIPATIENTOTHERNUMBER");
		 
		 this.URI_NHSOrg_PAS_CONSULTANT_CODE = env.getProperty("ORG.URINHSOrgPASCONSULTANTCODE");
		 
		 this.URI_NHSOrg_WARD_CODE = env.getProperty("ORG.URI_NHSOrgWARDCODE");
		 
		 this.URI_NHSOrg_HOSPITAL_CODE = env.getProperty("ORG.URINHSOrgHOSPITALCODE");
		 
		 this.uri_NHSOrg_EDMS_DocumentId = env.getProperty("ORG.uriNHSOrgEDMSDocumentId ");
		 
		 this.uri_NHSOrg_DocumentIndex = env.getProperty("ORG.uriNHSOrgDocumentIndex");
		 
		 this.uri_NHSOrg_Kodak_DocumentId = env.getProperty("ORG.uriNHSOrgKodakDocumentId");
		 
		 this.uriNHSOrgActivityId = env.getProperty("ORG.uriNHSOrgActivityId");
		 
		 this.uriNHSOrgEDISActivityId = env.getProperty("ORG.uriNHSOrgEDISActivityId");
		 
		 this.uriNHSOrgNorthgateDocumentId = env.getProperty("ORG.uriNHSOrgNorthgateDocumentId");
		 
		 this.URI_PATIENT_UN1 = env.getProperty("ORG.URIPATIENT_UN1");
		 
		 this.uriNHSOrgPASUSer = env.getProperty("ORG.uriNHSOrgPASUSer ");
		 
		 this.uriNHSOrgClinicalLetter = env.getProperty("ORG.uriNHSOrgClinicalLetter");
		 
		 this.uriNHSOrgDischargeLetter = env.getProperty("ORG.uriNHSOrgDischargeLetter");
		 
		 this.URI_NHSOrg_PAS_GP_CODE = env.getProperty("ORG.URINHSOrgPASGPCODE");
		 
		 this.uri_NHSOrg_EDMS_DrawerId = env.getProperty("ORG.uriNHSOrgEDMSDrawerId ");
		 
		 this.uri_NHSOrg_EDMS_DrawerId_Extension = env.getProperty("ORG.uriNHSOrgEDMSDrawerIdExtension");
		 
		 this.URI_NHSOrg_EDIS_REASON_CODE_SYSTEM = env.getProperty("ORG.URINHSOrgEDISREASONCODESYSTEM");
		 
		 this.URI_NHSOrg_SPECIALTY = env.getProperty("ORG.URINHSOrgSPECIALTY");
		 
		 this.URI_NHSOrg_CLINIC_CODE = env.getProperty("ORG.URINHSOrgCLINICCODE");
		 
		 this.URI_NHSOrg_REFERRAL_REASON = env.getProperty("ORG.URINHSOrgREFERRALREASON");
		 
		 this.URI_NHSOrg_REFERRAL_REASON_ACCPETED_DATE = env.getProperty("ORG.URINHSOrgREFERRALREASONACCPETEDDATE");
		 
		 this.uriNHSOrgLocation = env.getProperty("ORG.uriNHSOrgLocation");
		 
		 this.uriNHSOrgAppointmentId = env.getProperty("ORG.uriNHSOrgAppointmentId");
		 
	 }
	 
	 public String getURI_PATIENT_HOSPITAL_NUMBER()
	 {
		 return this.URI_PATIENT_HOSPITAL_NUMBER;
	 }
	 
	 public String getURI_PATIENT_OTHER_NUMBER()
	 {
		 return this.URI_PATIENT_OTHER_NUMBER;
	 }
	 
	 public String getURI_NHSOrg_PAS_CONSULTANT_CODE()
	 {
		 return this.URI_NHSOrg_PAS_CONSULTANT_CODE;
	 }
	 
	 public String getURI_NHSOrg_WARD_CODE()
	 {
		 return this.URI_NHSOrg_WARD_CODE;
	 }
	 
	 public String getURI_NHSOrg_HOSPITAL_CODE()
	 {
		 return this.URI_NHSOrg_HOSPITAL_CODE;
	 }
	 
	 public String geturi_NHSOrg_EDMS_DocumentId()
	 {
		 return this.uri_NHSOrg_EDMS_DocumentId;
	 }
	 public String geturi_NHSOrg_DocumentIndex()
	 {
		 return this.uri_NHSOrg_DocumentIndex;
	 }
	 public String geturi_NHSOrg_Kodak_DocumentId()
	 {
		 return this.uri_NHSOrg_Kodak_DocumentId;
	 }
	 
	 public String geturiNHSOrgActivityId()
	 {
		 return this.uriNHSOrgActivityId;
	 }
	 
	 public String geturiNHSOrgEDISActivityId()
	 {
		 return this.uriNHSOrgEDISActivityId;
	 }
	 
	 public String geturiNHSOrgNorthgateDocumentId()
	 {
		 return this.uriNHSOrgNorthgateDocumentId;
	 }
	 
	 public String getURI_PATIENT_UN1()
	 {
		 return this.URI_PATIENT_UN1;
	 }
	 
	 public String geturiNHSOrgPASUSer()
	 {
		 return this.uriNHSOrgPASUSer;
	 }
	 
	 public String geturiNHSOrgClinicalLetter()
	 {
		 return this.uriNHSOrgClinicalLetter;
	 }
	 
	 public String geturiNHSOrgDischargeLetter()
	 {
		 return this.uriNHSOrgDischargeLetter;
	 } 
	 
	 public String getURI_NHSOrg_PAS_GP_CODE()
	 {
		 return this.URI_NHSOrg_PAS_GP_CODE;
	 }
	 
	 public String geturi_NHSOrg_EDMS_DrawerId()
	 {
		 return this.uri_NHSOrg_EDMS_DrawerId;
	 }
	 public String geturi_NHSOrg_EDMS_DrawerId_Extension()
	 {
		 return this.uri_NHSOrg_EDMS_DrawerId_Extension;
	 }
	 
	 public String getURI_NHSOrg_EDIS_REASON_CODE_SYSTEM()
	 {
		 return this.URI_NHSOrg_EDIS_REASON_CODE_SYSTEM;
	 }
	 
	 public String getURI_NHSOrg_SPECIALTY()
	 {
		 return this.URI_NHSOrg_SPECIALTY;
	 }
	 
	 public String getURI_NHSOrg_CLINIC_CODE()
	 {
		 return this.URI_NHSOrg_CLINIC_CODE;
	 }
	 
	 public String getURI_NHSOrg_REFERRAL_REASON()
	 {
		 return this.URI_NHSOrg_REFERRAL_REASON;
	 }
	 
	 public String getURI_NHSOrg_REFERRAL_REASON_ACCPETED_DATE()
	 {
		 return this.URI_NHSOrg_REFERRAL_REASON_ACCPETED_DATE;
	 }
	 
	 public String geturiNHSOrgLocation()
	 {
		 return this.uriNHSOrgLocation;
	 }
	 
	 public String geturiNHSOrgAppointmentId()
	 {
		 return this.uriNHSOrgAppointmentId;
	 }
	 
}

