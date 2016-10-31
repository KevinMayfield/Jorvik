package uk.nhs.jorvik.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name="DOCUMENT_REFERENCE")
public class DocumentReferenceEntity extends BaseResource {
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="DOCUMENT_REFERENCE_ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RES_UPDATED", nullable = true)
	private Date myUpdated;
	public Date getUpdatedDate() {
		return myUpdated;
	}
	@Column(name = "classSnmdCT")
	private String classSnmdCT;
	public String getClassSnmdCT() { return this.classSnmdCT; }
	public void setClassSnmdCT(String classSnmdCT) { this.classSnmdCT = classSnmdCT; }
	
	@Column(name = "classSnmdCTName")
	private String classSnmdCTName;
	public String getClassSnmdCTName() { return this.classSnmdCTName; }
	public void setClassSnmdCTName(String classSnmdCTName) { this.classSnmdCTName = classSnmdCTName; }

	@Column(name = "typeSnmdCT")
	private String typeSnmdCT;
	public String getTypeSnmdCT() { return this.typeSnmdCT; }
	public void setTypeSnmdCT(String typeSnmdCT) { 	this.typeSnmdCT = typeSnmdCT; 	}
	
	@Column(name = "typeSnmdCTName")
	private String typeSnmdCTName;
	public String getTypeSnmdCTName() { return this.typeSnmdCTName; }
	public void setTypeSnmdCTName(String typeSnmdCTName) { 	this.typeSnmdCTName = typeSnmdCTName; 	}
	
	@Column(name = "PAS_PATIENT_ID")
	private String pasPatientId;
	public String getPASPatientId() { return this.pasPatientId; }
	public void setPASPatient(String pasPatientId) { 	this.pasPatientId = pasPatientId; 	}
	
	@Column(name = "created", nullable = true)
	private Date created;
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Column(name = "indexed", nullable = true)
	private Date indexed;
	public Date getIndexed() {
		return indexed;
	}
	public void setIndexed(Date indexed) {
		this.indexed = indexed;
	}
	
	@Column(name = "attachmentContentType")
	private String attachmentContentType;
	public String getAttachmentContentType() { return this.attachmentContentType; }
	public void setAttachmentContentType(String attachmentContentType) { 	this.attachmentContentType = attachmentContentType; 	}
	
	@Column(name = "attachmentUri")
	private String attachmentUri;
	public String getAttachmentUri() { return this.attachmentUri; }
	public void setAttachmentUri(String attachmentUri) { 	this.attachmentUri = attachmentUri; 	}
	
	@Column(name = "attachmentTitle")
	private String attachmentTitle;
	public String getAttachmentTitle() { return this.attachmentTitle; }
	public void setAttachmentTitle(String attachmentTitle) { 	this.attachmentTitle = attachmentTitle; 	}
	
	
	@OneToMany(mappedBy="documentReferenceId", targetEntity=DocumentReferenceIdentifier.class)
	//@OrderColumn (name = "IDENTIFIER_POSITION", nullable =false)
    private List<DocumentReferenceIdentifier> identifiers;
	public void setIdentifiers(List<DocumentReferenceIdentifier> identifiers) {
        this.identifiers = identifiers;
    }
	public List<DocumentReferenceIdentifier> getIdentifiers( ) {
		if (identifiers == null) {
	        identifiers = new ArrayList<DocumentReferenceIdentifier>();
	    }
        return this.identifiers;
    }
	public List<DocumentReferenceIdentifier> addIdentifier(DocumentReferenceIdentifier pi) { 
		identifiers.add(pi);
		return identifiers; }
	
	public List<DocumentReferenceIdentifier> removeIdentifier(DocumentReferenceIdentifier identifier){ 
		identifiers.remove(identifiers); return identifiers; }
	
	

	
	
}
