package uk.co.mayfieldis.jorvik.NHSSDS;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class NHSEntities {

	@DataField(pos = 1, trim = true)
	public String OrganisationCode;
	
	@DataField(pos = 2, trim = true)
	public String Name;
	
	@DataField(pos = 3, trim = true)
	public String NationalGrouping;
	
	@DataField(pos = 4, trim = true)
	public String HighLevelHealthGeography;
	
	@DataField(pos = 5, trim = true)
	public String AddressLine1;
	
	@DataField(pos = 6, trim = true)
	public String AddressLine2;
	
	@DataField(pos = 7, trim = true)
	public String AddressLine3;
	
	@DataField(pos = 8, trim = true)
	public String AddressLine4;
	
	@DataField(pos = 9, trim = true)
	public String AddressLine5;
	
	@DataField(pos = 10, trim = true)
	public String Postcode;
	
	@DataField(pos = 11, trim = true)
	public String OpenDate;
	
	@DataField(pos = 12, trim = true)
	public String CloseDate;
	
	@DataField(pos = 13, trim = true)
	public String StatusCode;
	
	@DataField(pos = 14, trim = true)
	public String OrganisationSubTypeCode;
	
	@DataField(pos = 15, trim = true)
	public String ParentOrganisationCode;
	
	@DataField(pos = 16, trim = true)
	public String JoinParentDate;
	
	@DataField(pos = 17, trim = true)
	public String LeftParentDate;
	
	@DataField(pos = 18, trim = true)
	public String ContactTelephoneNumber;
	
	@DataField(pos = 19, trim = true)
	private String Field19;
	
	@DataField(pos = 20, trim = true)
	private String Field20;

	@DataField(pos = 21, trim = true)
	private String Field21;
	
	@DataField(pos =22, trim = true)
	public String AmendedRecordIndicator;
	
	@DataField(pos = 23, trim = true)
	private String Field23;
	
	@DataField(pos = 24, trim = true)
	public String CurrentCareOrganisation;
	
	@DataField(pos = 25, trim = true)
	private String Field25;
	
	@DataField(pos = 26, trim = true)
	private String Field26;
	
	@DataField(pos = 27, trim = true)
	private String Field27;
}
