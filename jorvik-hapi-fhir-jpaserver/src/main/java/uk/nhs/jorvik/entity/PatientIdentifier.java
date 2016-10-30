package uk.nhs.jorvik.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="PATIENT_IDENTIFIER")
public class PatientIdentifier extends BaseIdentifier {
	
	public PatientIdentifier(PatientEntity ep) {
		//setPatientId(ep); 
		this.patient = ep;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PATIENT_IDENTIFIER_ID" )
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;
	
	@Column(name ="PATIENT_ID")
	private Integer patientId;
	
	@ManyToOne
	@JoinColumn ( name = "PATIENT_ID" )
	private PatientEntity patient;
	
    public Integer getPatientId() {
        return patient.getId();
    }

    public void setPatientId(PatientEntity patient) {
        this.patientId = patient.getId();
    }
}
