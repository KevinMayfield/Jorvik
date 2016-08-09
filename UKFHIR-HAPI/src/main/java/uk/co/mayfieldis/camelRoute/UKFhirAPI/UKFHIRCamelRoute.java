package uk.co.mayfieldis.camelRoute.UKFhirAPI;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import uk.co.mayfieldis.jorvik.UKFHIR.FHIRDocumentReferenceProcess;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;

@Component
@PropertySource("classpath:HAPIHL7FHIR.properties")
public class UKFHIRCamelRoute extends RouteBuilder {

	@Autowired
	protected Environment env;
	
    @Override
    public void configure() 
    {
    	FhirContext ctx = FhirContext.forDstu3();
    	
    	NHSTrustFHIRCodeSystems TrustFHIRSystems = new NHSTrustFHIRCodeSystems();
    	TrustFHIRSystems.setValues(env);
    	
    	FHIRDocumentReferenceProcess fhirDocumentReferenceProcess = new FHIRDocumentReferenceProcess(ctx, TrustFHIRSystems);
    	    	
    	
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
			.to("activemq:HAPIFHIRAPI");
			
		from("direct:ping")
			.routeId("Ping")
			.transform(constant("Pong"));
		
		
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
