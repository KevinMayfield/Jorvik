package uk.co.mayfieldis.camelRoute.UKFhirHapi;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.UKFHIR.FHIRDocumentReferenceProcess;
import uk.co.mayfieldis.jorvik.core.EnrichDocumentReferencewithEncounter;
import uk.co.mayfieldis.jorvik.core.EnrichDocumentReferencewithPatient;
import uk.co.mayfieldis.jorvik.core.EnrichDocumentReferencewithPractitioner;


public class UKFHIRCamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	FHIRDocumentReferenceProcess fhirDocumentReferenceProcess = new FHIRDocumentReferenceProcess();
    	EnrichDocumentReferencewithPatient enrichDocumentReferencewithPatient = new EnrichDocumentReferencewithPatient();
    	EnrichDocumentReferencewithPractitioner enrichDocumentReferencewithPractitioner = new EnrichDocumentReferencewithPractitioner();
    	EnrichDocumentReferencewithEncounter enrichDocumentReferencewithEncounter = new EnrichDocumentReferencewithEncounter();
    	
		errorHandler(deadLetterChannel("direct:error")
    		.maximumRedeliveries(2));
        	    
	    from("direct:error")
        	.routeId("UK FHIR HAPI Fail Handler")
        	.to("log:uk.co.mayfieldis.camelRoute.UKFhirHapi.UKFHIRCamelRoute?level=ERROR&showAll=true");
	
	    restConfiguration()
	    	.component("servlet")
	    	.bindingMode(RestBindingMode.off)
	    	.contextPath("UKFHIR-HAPI/rest")
	    	.port(8181)
	    	.dataFormatProperty("prettyPrint","true");
	    
		rest("/Mailbox")
			.post("/")
				.route()
				.routeId("Mailbox POST")
				.to("direct:ping");
		
		rest("/DocumentReference")
			.post("/")
			.route()
				.routeId("DocumentReference POST")
				.process(fhirDocumentReferenceProcess)
				.choice()
					.when(header("FHIRPatient").isNotNull())
						.enrich("vm:lookupPatient",enrichDocumentReferencewithPatient)
				.end()
				.choice()
					.when(header("FHIRPractitioner").isNotNull())
						.enrich("vm:lookupConsultant",enrichDocumentReferencewithPractitioner)
				.end()
				.choice()
					.when(header("FHIRGP").isNotNull())
						.enrich("vm:lookupGP",enrichDocumentReferencewithPractitioner)
				.end()
				.choice()
					.when(header("FHIREncounter").isNotNull())
						.enrich("vm:lookupEncounter",enrichDocumentReferencewithEncounter)
				.end()
				.to("activemq:FileFHIR")
				.to("direct:ping");
		
		from("direct:ping")
			.routeId("Ping")
			.transform(constant("Pong"));
		
		
	 	from("vm:lookupLocation")
		.routeId("Loookup FHIR Location")
		.setBody(simple(""))
    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
    	.setHeader(Exchange.HTTP_BASE_URI, simple("", String.class))
    	.setHeader(Exchange.HTTP_PATH, simple("/Location",String.class))
    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTLocation+"|${header.FHIRLocation}&_format=xml",String.class))
    	.to("vm:HAPIFHIR");
	    	
	
	
		from("vm:lookupGP")
			.routeId("Lookup FHIR Practitioner (GP)")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_URI, simple("", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_GMP_CODE+"|${header.FHIRGP}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
		
		from("vm:lookupConsultant")
			.routeId("Lookup FHIR Practitioner (Consultant)")
	    	.setBody(simple(""))
	    	//.log("GET /Practitioner?identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}")
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_URI, simple("/Practitioner", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
	    	
		from("vm:lookupPatient")
			.routeId("Lookup FHIR Patient")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
			.setHeader(Exchange.HTTP_URI, simple("/Patient", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.URI_PATIENT_DISTRICT_NUMBER+"|${header.FHIRPatient}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
		
		from("vm:lookupEncounter")
			.routeId("Lookup FHIR Encounter")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
			.setHeader(Exchange.HTTP_URI, simple("/Encounter", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Encounter",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTActivityId+"|${header.FHIREncounter}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
		
		from("vm:HAPIFHIR")
			.routeId("HAPI FHIR")
			.to("http:localhost:8181/hapi-fhir-jpaserver/baseDstu2?throwExceptionOnFailure=false&connectionsPerRoute=60&bridgeEndpoint=true")
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.hl7v2.hapi.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
			.end();
    }
}
