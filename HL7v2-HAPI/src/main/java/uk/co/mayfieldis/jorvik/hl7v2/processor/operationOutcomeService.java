package uk.co.mayfieldis.jorvik.hl7v2.processor;


import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.OperationOutcome;
import org.hl7.fhir.instance.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.instance.model.OperationOutcome.IssueType;

import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;


public class operationOutcomeService {
	
	public operationOutcomeService() {
        
    }
	
	public String getOutcome(Integer errorCode, String errorDecription, String contentType) {
        
		OperationOutcome outcome = new OperationOutcome();
		
		outcome.setId(errorCode.toString());
		if  ((errorCode < 499) && (errorCode >= 400))
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.WARNING);
		}
		else if (errorCode < 400)
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.INFORMATION);
		}
		else
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.ERROR);
		}
		
		
		CodeableConcept details = new CodeableConcept();
		details.setText(errorDecription)
			.addCoding()
			.setCode(errorCode.toString());
			
		outcome.getIssue().get(0)
			.setCode(IssueType.VALUE)
			.setDetails(details);
		
		String output=null;
		
		if (contentType==null)
		{
		  contentType="application/json";	
		}
		
		if (contentType.contains("json"))	
		{
			output = ResourceSerialiser.serialise(outcome, ParserType.JSON);
		}
		else
		{
			output = ResourceSerialiser.serialise(outcome, ParserType.XML);
		}
		
        return output;
    }
}
