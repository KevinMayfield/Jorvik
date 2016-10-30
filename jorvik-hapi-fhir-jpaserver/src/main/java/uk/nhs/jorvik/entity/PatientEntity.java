package uk.nhs.jorvik.entity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="PATIENT")
public class PatientEntity extends BaseResource {
	
	

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
		
		@Column(name = "gender")
		private String gender;
		public void setGender(String gender)
		{  this.gender = gender; }
		public String getGender()  {  return this.gender;  }
		
		@OneToMany(mappedBy="patientId", targetEntity=PatientIdentifier.class)
		//@OrderColumn (name = "IDENTIFIER_POSITION", nullable =false)
	    private Collection<PatientIdentifier> identifiers;
		public void setIdentifiers(List<PatientIdentifier> identifiers) {
	        this.identifiers = identifiers;
	    }
		public Collection<PatientIdentifier> getIdentifiers( ) {
			if (identifiers == null) {
		        identifiers = new ArrayList<PatientIdentifier>();
		    }
	        return this.identifiers;
	    }
		public Collection<PatientIdentifier> addIdentifier(PatientIdentifier pi) { 
			identifiers.add(pi);
			return identifiers; }
		public Collection<PatientIdentifier> removeIdentifier(PatientIdentifier identifier) { identifiers.remove(identifiers); return identifiers; }
		
		
		
		
	
}
