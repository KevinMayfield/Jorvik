package uk.co.mayfieldis.jorvik.hl7v2.processor;




import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;


import ca.uhn.fhir.context.FhirContext;


public class operationOutcomeService {
	
	public operationOutcomeService(FhirContext ctx)
	{
		this.ctx = ctx;
		
	}
	
	private FhirContext ctx;
	
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
		
		String Response=null;
		
		if (contentType==null)
		{
		  contentType="application/json";	
		}
		
		if (contentType.contains("json"))	
		{
			Response = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
			//output = ResourceSerialiser.serialise(outcome, ParserType.JSON);
		}
		else
		{
			Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(outcome);
			//output = ResourceSerialiser.serialise(outcome, ParserType.XML);
		}
		
        return Response;
    }
}
