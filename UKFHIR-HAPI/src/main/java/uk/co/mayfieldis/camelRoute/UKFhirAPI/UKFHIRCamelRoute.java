package uk.co.mayfieldis.camelRoute.UKFhirAPI;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import uk.co.mayfieldis.jorvik.UKFHIR.FHIRDocumentReferenceProcess;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.camel.EnrichDocumentReferencewithDocumentReference;
import uk.co.mayfieldis.jorvik.core.camel.EnrichDocumentReferencewithEncounter;
import uk.co.mayfieldis.jorvik.core.camel.EnrichDocumentReferencewithPatient;
import uk.co.mayfieldis.jorvik.core.camel.EnrichDocumentReferencewithPractitioner;

@Component
@PropertySource("classpath:HAPIHL7FHIR.properties")
public class UKFHIRCamelRoute extends RouteBuilder {

	@Autowired
	protected Environment env;
	
    @Override
    public void configure() 
    {
    	NHSTrustFHIRCodeSystems TrustFHIRSystems = new NHSTrustFHIRCodeSystems();
    	TrustFHIRSystems.setValues(env);
    	
    	FHIRDocumentReferenceProcess fhirDocumentReferenceProcess = new FHIRDocumentReferenceProcess();
    	fhirDocumentReferenceProcess.env = env;
    	fhirDocumentReferenceProcess.TrustFHIRSystems = TrustFHIRSystems;
    	
    	EnrichDocumentReferencewithPatient enrichDocumentReferencewithPatient = new EnrichDocumentReferencewithPatient();
    	EnrichDocumentReferencewithPractitioner enrichDocumentReferencewithPractitioner = new EnrichDocumentReferencewithPractitioner();
    	EnrichDocumentReferencewithEncounter enrichDocumentReferencewithEncounter = new EnrichDocumentReferencewithEncounter();
    	EnrichDocumentReferencewithDocumentReference enrichDocumentReferencewithDocumentReference = new EnrichDocumentReferencewithDocumentReference();
    	
		errorHandler(deadLetterChannel("direct:error")
    		.maximumRedeliveries(2));
        	    
	    from("direct:error")
        	.routeId("UK FHIR HAPI Fail Handler")
        	.to("log:uk.co.mayfieldis.camelRoute.UKFhirAPI.UKFHIRCamelRoute?level=ERROR&showAll=true");
	
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
				.to("activemq:DocumentReference");
		
		rest("/Ping")
			.post("/")
				.route()
				.routeId("Ping POST")
				.to("direct:ping");
		
		from(env.getProperty("NHSITK.Path"))
			.routeId("DocumentReference POST File")
			.setHeader(Exchange.HTTP_METHOD, simple("POST", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("DocumentReference",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("",String.class))
	    	.setHeader(Exchange.CONTENT_TYPE,simple("application/xml+fhir",String.class))
			.to("activemq:DocumentReference");
		
		from("activemq:DocumentReference")
		.routeId("DocumentReference POST ActiveMQ")
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
			.enrich("vm:lookupResource",enrichDocumentReferencewithDocumentReference)
			.to("activemq:HAPIFHIRAPI");
			
		from("direct:ping")
			.routeId("Ping")
			.transform(constant("Pong"));
		
		
	 	from("vm:lookupLocation")
			.routeId("Loookup FHIR Location")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_BASE_URI, simple("", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Location",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+TrustFHIRSystems.geturiNHSOrgLocation()+"|${header.FHIRLocation}&_format=xml",String.class))
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
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_URI, simple("/Practitioner", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
	    	
		from("vm:lookupPatient")
			.routeId("Lookup FHIR Patient")
			.log("Patient.identifier=${header.FHIRPatient}")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
			.setHeader(Exchange.HTTP_URI, simple("/Patient", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier=${header.FHIRPatient}",String.class))
	    	.to("vm:HAPIFHIR");
		
		from("vm:lookupEncounter")
			.routeId("Lookup FHIR Encounter")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
			.setHeader(Exchange.HTTP_URI, simple("/Encounter", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Encounter",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+TrustFHIRSystems.geturiNHSOrgActivityId()+"|${header.FHIREncounter}&_format=xml",String.class))
	    	.to("vm:HAPIFHIR");
		
		 from("vm:lookupResource")
	    	.routeId("Lookup FHIR Resources")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("${header.FHIRResource}",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("${header.FHIRQuery}",String.class))
	    	.to("vm:HAPIFHIR");
		
		from("vm:HAPIFHIR")
			.routeId("HAPI FHIR")
			.to(env.getProperty("HAPIFHIR.ServerNoExceptions"))
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.camelRoute.UKFhirAPI.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
				.when(simple("${in.header.CamelHttpResponseCode} == 400"))
					.to("log:uk.co.mayfieldis.camelRoute.UKFhirAPI.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 400")
			.end();
		
		from("activemq:HAPIFHIRAPI")
			.routeId("HAPI FHIR API")
			.to(env.getProperty("HAPIFHIR.ServerNoExceptions"))
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.camelRoute.UKFhirAPI.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
				.when(simple("${in.header.CamelHttpResponseCode} == 400"))
					.to("log:uk.co.mayfieldis.camelRoute.UKFhirAPI.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 400")
			.end();
    }
}
