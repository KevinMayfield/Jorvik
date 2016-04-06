package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class NHSConsultantEntities {

	@DataField(pos = 1, trim = true)
	public String GMCCode;

	@DataField(pos = 2, trim = true)
	public String PractitionerCode;
	
	@DataField(pos = 3, trim = true)
	public String Surname;

	@DataField(pos = 4, trim = true)
	public String Initials;

	@DataField(pos = 5, trim = true)
	public String Sex;
	
	@DataField(pos = 6, trim = true)
	public String SpecialityFunctionCode;

	@DataField(pos = 7, trim = true)
	public String PractitionerType;

	@DataField(pos = 8, trim = true)
	public String LocationOrganisationCode;

	@DataField(pos = 9, trim = true)
	public String Field9;

	@DataField(pos = 10, trim = true)
	public String Field10;

	@DataField(pos = 11, trim = true)
	public String Field11;

	@DataField(pos = 12, trim = true)
	public String Field12;

	@DataField(pos = 13, trim = true)
	public String Field13;
 
}
