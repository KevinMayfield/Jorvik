package uk.nhs.jorvik.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hl7.fhir.instance.model.api.IBaseResource;


import ca.uhn.fhir.context.FhirVersionEnum;


@MappedSuperclass
public abstract class BaseResource implements IBaseResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Column(name = "RES_DELETED_AT", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date myDeleted;
	
	@Column(name = "RES_VERSION", nullable = true, length = 7)
	@Enumerated(EnumType.STRING)
	private FhirVersionEnum myFhirVersion;
	
	
	
	public Date getDeleted() {
		return myDeleted;
	}
	
	
	
	public void setDeleted(Date theDate) {
		myDeleted = theDate;
	}
	
	public void setFhirVersion(FhirVersionEnum theFhirVersion) {
		myFhirVersion = theFhirVersion;
	}


	
	
}
