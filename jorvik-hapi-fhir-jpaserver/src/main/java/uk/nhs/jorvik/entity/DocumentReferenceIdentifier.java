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
@Table(name="DOCUMENT_REFERENCE_IDENTIFIER")
public class DocumentReferenceIdentifier extends BaseIdentifier {
	
	
	public DocumentReferenceIdentifier() {
		
		
	}
	public DocumentReferenceIdentifier(DocumentReferenceEntity edr) {
		
		this.documentReference = edr;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "DOCUMENT_REFERENCE_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;

	/*
	@Column(name = "IDENTIFIER_POSITION",updatable=false, insertable = false )
	public Integer getIdentifierPosition() { return identifierPosition; }
	public void setIdentifierPosition(Integer identifierPosition) { this.identifierPosition = identifierPosition; }
	private Integer identifierPosition;
	*/
	
	@Column(name="MASTER_IDENTIFIER")
	public Boolean getMasterIdentifier() { return masterIdentifier; }
	public void setMasterIdentifier(Boolean masterIdentifier ) { this.masterIdentifier = masterIdentifier; }
	private Boolean masterIdentifier;

	//@Column(name ="DOCUMENT_REFERENCE_ID")
	//private Integer documentReferenceId;
	
	@ManyToOne
	@JoinColumn (name = "DOCUMENT_REFERENCE_ID")
	private DocumentReferenceEntity documentReference;

	// remember this is getting picked up by the mappedBy column
	 public Integer getDocumentReferenceId() {
	        return documentReference.getId();
	    }

	    public void setDocumentReferenceId(Integer documentReference) {
	        //this.documentReference = documentReference;
	    }

}
