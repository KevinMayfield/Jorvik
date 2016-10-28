package uk.nhs.jorvik.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import ca.uhn.fhir.context.FhirVersionEnum;

@Entity
@Table(name="PATIENT")
public class PatientEntity extends BaseResource {
	
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PATIENT_ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RES_UPDATED", nullable = true)
	private Date myUpdated;
	public Date getUpdatedDate() { return myUpdated; }
	
		@Column(name = "family_name")
		private String familyName;
		public void setFamilyName(String familyName) {   this.familyName = familyName;   }
		public String getFamilyName() { return this.familyName;  }
		
		@Column(name = "given_name")
		private String givenName;
		public void setGivenName(String givenName)
		{  this.givenName = givenName; }
		public String getGivenName()  {  return this.givenName;  }
		@Override
		public IIdType getIdElement() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public IBaseMetaType getMeta() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public FhirVersionEnum getStructureFhirVersionEnum() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public IBaseResource setId(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public IBaseResource setId(IIdType arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<String> getFormatCommentsPost() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<String> getFormatCommentsPre() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public boolean hasFormatComment() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}
		  
	
}
