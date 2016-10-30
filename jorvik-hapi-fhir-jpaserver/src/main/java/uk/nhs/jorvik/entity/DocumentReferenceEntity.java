package uk.nhs.jorvik.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name="DOCUMENT_REFERENCE")
public class DocumentReferenceEntity extends BaseResource {
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="DOCUMENT_ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RES_UPDATED", nullable = false)
	private Date myUpdated;
	public Date getUpdatedDate() {
		return myUpdated;
	}
	
	@OneToMany 
	@JoinColumn (name = "ID", nullable =false)
	@OrderColumn (name = "IDENTIFIER_POSITION", nullable =false)
	private List<DocumentReferenceIdentifier> identifiers = new ArrayList<DocumentReferenceIdentifier>();
	public List<DocumentReferenceIdentifier> getIdentifiers() { return identifiers; }
	public void setIdentifiers(List<DocumentReferenceIdentifier> identifiers) { this.identifiers = identifiers; }
	
	
	/*
	@OneToOne
	private DocumentReferenceIdentifier masterIdentifier;
	public BaseIdentifier getMasterIdentifier() { return this.masterIdentifier; } 
	public void setMasterIdentifier(DocumentReferenceIdentifier identifier) { this.masterIdentifier = identifier; }
	*/
	
	@Column(name = "class")
	private String _class;
	public String _getClass() { return this._class; }
	public void _setClass(String _class) { this._class = _class; }
	

	@Column(name = "type")
	private String type;
	public String getType() { return this.type; }
	public void setType(String type) { 	this.type = type; 	}
	
	
	
}
