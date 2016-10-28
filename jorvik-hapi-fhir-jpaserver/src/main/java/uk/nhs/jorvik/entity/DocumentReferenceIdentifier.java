package uk.nhs.jorvik.entity;

import javax.annotation.Nonnull;
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
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;

	@Column(name = "IDENTIFIER_POSITION",updatable=false, insertable = false )
	public Integer getIdentifierPosition() { return identifierPosition; }
	public void setIdentifierPosition(Integer identifierPosition) { this.identifierPosition = identifierPosition; }
	private Integer identifierPosition;
	
	@Column(name="MASTER_IDENTIFIER")
	public Boolean getMasterIdentifier() { return masterIdentifier; }
	public void setMasterIdentifier(Boolean masterIdentifier ) { this.masterIdentifier = masterIdentifier; }
	private Boolean masterIdentifier;

	
	@ManyToOne
	@JoinColumn ( name = "ID",updatable=false, insertable = false )
	@Nonnull
	protected DocumentReferenceEntity documentReference;

	

}
