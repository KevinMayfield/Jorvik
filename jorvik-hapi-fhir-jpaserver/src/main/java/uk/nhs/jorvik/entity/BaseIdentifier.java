package uk.nhs.jorvik.entity;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseIdentifier {
	
	@Column(name = "system")
	private String system;
	public void setSystem(String system) { 	this.system = system; }
	public String getSystem() { return this.system; }
	
	@Column(name = "value")
	private String value;
	public void setValue(String value) { this.value = value; } 	
	public String getValue() { 	return this.value; }
	
	@Column(name = "ORDER")
	private Integer order;
	
	
	
}
