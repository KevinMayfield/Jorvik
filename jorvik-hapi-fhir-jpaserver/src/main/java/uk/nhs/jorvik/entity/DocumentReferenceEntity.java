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

import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import ca.uhn.fhir.context.FhirVersionEnum;


@Entity
@Table(name="DOCUMENT_REFERENCE")
public class DocumentReferenceEntity extends BaseResource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
