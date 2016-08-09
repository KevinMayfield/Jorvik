package uk.co.mayfieldis.jorvik.camelRoute.hl7v2FHIR;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.jorvik.core.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEncounter;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEpisodeOfCare;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA05A38toAppointment;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA28A31toPatient;

import static org.apache.camel.component.hl7.HL7.ack;
import org.apache.camel.ExchangePattern;

@Component
@PropertySource("classpath:HAPIHL7.properties")
public class HL7v2CamelRoute extends RouteBuilder {

	@Autowired
	protected Environment env;
	
    @Override
    public void configure() 
    {
    	NHSTrustFHIRCodeSystems TrustFHIRSystems = new NHSTrustFHIRCodeSystems();
    	
    	// only use one context. Expensive to create - possibly look at making this config.
    	FhirContext ctx = FhirContext.forDstu3();
    	
    	TrustFHIRSystems.setValues(env);
    	
    	HapiContext hapiContext = new DefaultHapiContext();
    	
    	hapiContext.getParserConfiguration().setValidating(false);
    	HL7DataFormat hl7 = new HL7DataFormat();
    	
    	hl7.setHapiContext(hapiContext);
    	
    	//LightWithFHIR lightWithFHIR = new LightWithFHIR(); 
    	//MFNM05toFHIRLocation enrichMFNM05withLocation = new MFNM05toFHIRLocation();
    	ADTA28A31toPatient adta28a31toPatient = new ADTA28A31toPatient(ctx, env);  
    	ADTA01A04A08toEpisodeOfCare adta01a04a08toEpisodeOfCare = new ADTA01A04A08toEpisodeOfCare(ctx,this.env);
    	ADTA01A04A08toEncounter adta01a04a08toEncounter = new ADTA01A04A08toEncounter(ctx,this.env,TrustFHIRSystems);
    	ADTA05A38toAppointment adta05a38toAppointment = new ADTA05A38toAppointment(ctx,this.env,TrustFHIRSystems);
    	//MFNM02toFHIRPractitioner mfnm02PractitionerProcessor = new MFNM02toFHIRPractitioner(ctx,TrustFHIRSystems);
    	//MFNM05toFHIRLocation mfnm05LocationProcessor = new MFNM05toFHIRLocation(ctx,TrustFHIRSystems);
    	
    	
    	onException(org.apache.
    			camel.CamelAuthorizationException.class)
    		.routeId("AuthourisationError")
    		.handled(true)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.processor.OperationOutcome?level=WARN")
    		.to("bean:outcome?method=getOutcome(401,'Unauthorized'+${exception.policyId}, '')");
    	
    	RedeliveryPolicyDefinition retryPolicyDef = new RedeliveryPolicyDefinition();
    	
    	retryPolicyDef
    		.setRedeliveryDelay("100");
    	retryPolicyDef.setMaximumRedeliveries("3");
    	
    	onException(org.apache.camel.http.common.HttpOperationFailedException.class)
    		.maximumRedeliveries(3)
    		.handled(false)
    		.log("Error Message = ${exception.message}")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.httpError?showAll=true&multiline=true&level=WARN");
    	
    	errorHandler(defaultErrorHandler().maximumRedeliveries(3));
    	
    	from(env.getProperty("NHSITK.Path"))
    		.routeId("HL7v2 File")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT"))
					.wireTap("vm:ADT")
					.end()
				.when(header("CamelHL7MessageType").isEqualTo("MFN"))
					.wireTap("vm:MFN")
					.end()
			.end();
			
    	from("hl7MinaListener")
    		.routeId("HL7v2")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT"))
					.wireTap("vm:ADT")
					.end()
				.when(header("CamelHL7MessageType").isEqualTo("MFN"))
					.wireTap("vm:MFN")
					.end()
			.end()
    		.transform(ack());
    	
    	from("vm:MFN")
			.routeId("MFN")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");
    	
    	/*
    	from("vm:MFN")
    		.routeId("MFN")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M02")).to(ExchangePattern.InOnly,"activemq:MFN_M02")
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M05")).to(ExchangePattern.InOnly,"activemq:MFN_M05")
    		.end();
    	
    	from("activemq:MFN_M02")
			.routeId("MFN_M02 Consultants")
			.process(mfnm02PractitionerProcessor)
			.log("Org = ${header.FHIROrganisationCode}")
			.enrich("vm:lookupOrganisation",consultantEnrichwithOrganisation)
	    	.enrich("vm:lookupResource",enrichUpdateType)
			.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");
	
		from("activemq:MFN_M05")
			.routeId("MFN_M05 Clinic Locations")
			.process(mfnm05LocationProcessor)
			.enrich("vm:lookupOrganisation",enrichLocationwithOrganisation)
	    	.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichLocationwithLocation)
			.end()
	    	.enrich("vm:lookupResource",enrichUpdateType)
			.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");
	    */
    	from("vm:ADT")
    		.routeId("ADT")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A01")).to(ExchangePattern.InOnly, "activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A02")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A03")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to(ExchangePattern.InOnly,"activemq:ADT_A05A38")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A08")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A11")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A12")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A13")).to(ExchangePattern.InOnly,"activemq:ADT_A01A04A08Encounter")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A38")).to(ExchangePattern.InOnly,"activemq:ADT_A05A38") 
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to(ExchangePattern.InOnly,"activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to(ExchangePattern.InOnly,"activemq:ADT_A28A31")
			/*	.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40") */
			.end();
    	
    	
    	
    		
    	// Demographics 
		from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31 Demographics")
			.process(adta28a31toPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");
		
		from("activemq:ADT_A01A04A08")
			.routeId("ADT_A01A04A08")
			.multicast()
				.to(ExchangePattern.InOnly,"activemq:ADT_Episode","activemq:ADT_A01A04A08Encounter");
		
		from("activemq:ADT_Episode")
			.routeId("ADT_Episode")
			.process(adta01a04a08toEpisodeOfCare)
			//Only process if episode Id is supplied
			.choice()
				.when(header("FHIREpisode").isNotNull())
					.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");
		
    	// Encounters and Episodes
		from("activemq:ADT_A01A04A08Encounter")
			.routeId("ADT_A01A04A08 Encounters")
			.process(adta01a04a08toEncounter)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");
		
		// Appointments and pre op
		
		from("activemq:ADT_A05A38")
			.routeId("ADT_A05A38")
			.multicast()
				.to(ExchangePattern.InOnly,"activemq:ADT_A05A38Appointment","activemq:ADT_Episode");
		
		
		from("activemq:ADT_A05A38Appointment")
			.routeId("ADT_A05A38 Appointments")
			.process(adta05a38toAppointment)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to(ExchangePattern.InOnly,"activemq:HAPIHL7v2");

		// Store resources
		
		from("activemq:FileFHIR")
			.routeId("FileStore")
			.to(env.getProperty("HAPIFHIR.FileStore")+"${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
	
		
		
		from("activemq:HAPIHL7v2")
			.routeId("HAPI FHIR MQ")
			.onException(org.apache.camel.http.common.HttpOperationFailedException.class).maximumRedeliveries(0).end()
			.to(env.getProperty("HAPIFHIR.ServerNoExceptions"))
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.hl7v2.hapi.activemq.HAPIHL7v2?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
			.end()
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 400"))
					.to("log:uk.co.mayfieldis.hl7v2.hapi.vm.HAPIFHIR?showAll=true&multiline=true&level=WARN")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 400")
			.end();
	
		
	
    	    	
    }
}
