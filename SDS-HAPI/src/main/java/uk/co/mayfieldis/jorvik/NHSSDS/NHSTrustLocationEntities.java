package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class NHSTrustLocationEntities {

	@DataField(pos = 1, trim = true)
	public String LocalCode;

	@DataField(pos = 2, trim = true)
	public String Description;
	
	// OF = Clinic, HU = Ward, HOSP = Hospital
	@DataField(pos = 3, trim = true)
	public String Type;

	@DataField(pos = 4, trim = true)
	public String PartOf;
	 
	@DataField(pos = 5, trim = true)
	public String managingOrganization;
}
