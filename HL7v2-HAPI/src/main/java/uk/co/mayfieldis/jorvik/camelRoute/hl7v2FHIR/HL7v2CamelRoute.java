package uk.co.mayfieldis.jorvik.camelRoute.hl7v2FHIR;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.EnrichAppointmentwithAppointment;
import uk.co.mayfieldis.jorvik.core.EnrichAppointmentwithLocation;
import uk.co.mayfieldis.jorvik.core.EnrichAppointmentwithPatient;
import uk.co.mayfieldis.jorvik.core.EnrichAppointmentwithPractitioner;
import uk.co.mayfieldis.jorvik.core.EnrichConsultantwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithAppointment;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithEncounter;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithEpisodeOfCare;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithLocation;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithPatient;
import uk.co.mayfieldis.jorvik.core.EnrichEncounterwithPractitioner;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithLocation;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichPatientwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichPatientwithPatient;
import uk.co.mayfieldis.jorvik.core.EnrichPatientwithPractitioner;
import uk.co.mayfieldis.jorvik.core.EnrichwithUpdateType;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEncounter;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA05A38toAppointment;
import uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA28A31toPatient;
import uk.co.mayfieldis.jorvik.hl7v2.processor.EncountertoEpisodeOfCare;
import uk.co.mayfieldis.jorvik.hl7v2.processor.MFNM02toFHIRPractitioner;
import uk.co.mayfieldis.jorvik.hl7v2.processor.MFNM05toFHIRLocation;

import static org.apache.camel.component.hl7.HL7.ack;
import org.apache.camel.Exchange;

@Component
@PropertySource("classpath:HAPIHL7.properties")
public class HL7v2CamelRoute extends RouteBuilder {

	@Autowired
	protected Environment env;
	
    @Override
    public void configure() 
    {
    	NHSTrustFHIRCodeSystems TrustFHIRSystems = new NHSTrustFHIRCodeSystems();
    	TrustFHIRSystems.setValues(env);
    	
    	HapiContext hapiContext = new DefaultHapiContext();
    	
    	hapiContext.getParserConfiguration().setValidating(false);
    	HL7DataFormat hl7 = new HL7DataFormat();
    	
    	hl7.setHapiContext(hapiContext);
    	
    	//LightWithFHIR lightWithFHIR = new LightWithFHIR(); 
    	//MFNM05toFHIRLocation enrichMFNM05withLocation = new MFNM05toFHIRLocation();
    	ADTA28A31toPatient adta28a31toPatient = new ADTA28A31toPatient();  
    	adta28a31toPatient.TrustFHIRSystems = TrustFHIRSystems;
    	adta28a31toPatient.env = this.env;
    	
    	ADTA01A04A08toEncounter adta01a04a08toEncounter = new ADTA01A04A08toEncounter();
    	adta01a04a08toEncounter.TrustFHIRSystems =TrustFHIRSystems;
    	adta01a04a08toEncounter.env = this.env;
    	
    	ADTA05A38toAppointment adta05a38toAppointment = new ADTA05A38toAppointment();
    	adta05a38toAppointment.TrustFHIRSystems =TrustFHIRSystems;
    	adta05a38toAppointment.env = this.env;
    	
    	MFNM02toFHIRPractitioner mfnm02PractitionerProcessor = new MFNM02toFHIRPractitioner();
    	mfnm02PractitionerProcessor.TrustFHIRSystems = TrustFHIRSystems;
    	
    	MFNM05toFHIRLocation mfnm05LocationProcessor = new MFNM05toFHIRLocation();
    	mfnm05LocationProcessor.TrustFHIRSystems = TrustFHIRSystems;
    	
    	EncountertoEpisodeOfCare encountertoEpisodeOfCare = new EncountertoEpisodeOfCare();
    	encountertoEpisodeOfCare.env = this.env;
    	
    	EnrichLocationwithLocation enrichLocationwithLocation = new EnrichLocationwithLocation();
    	EnrichLocationwithOrganisation enrichLocationwithOrganisation = new EnrichLocationwithOrganisation();
    	EnrichwithUpdateType enrichUpdateType = new EnrichwithUpdateType();
    	
    	EnrichPatientwithOrganisation enrichPatientwithOrganisation = new EnrichPatientwithOrganisation();
    	EnrichPatientwithPractitioner enrichPatientwithPractitioner = new EnrichPatientwithPractitioner();
    	EnrichPatientwithPatient enrichPatientwithPatient = new EnrichPatientwithPatient();
    	
    	EnrichEncounterwithPatient enrichEncounterwithPatient = new EnrichEncounterwithPatient();
    	EnrichEncounterwithPractitioner enrichEncounterwithPractitioner = new EnrichEncounterwithPractitioner();
    	EnrichEncounterwithOrganisation enrichEncounterwithOrganisation = new EnrichEncounterwithOrganisation();
    	EnrichEncounterwithLocation enrichEncounterwithLocation = new EnrichEncounterwithLocation();
    	EnrichEncounterwithAppointment enrichEncounterwithAppointment = new EnrichEncounterwithAppointment();
    	EnrichEncounterwithEncounter enrichEncounterwithEncounter = new EnrichEncounterwithEncounter();
    	EnrichEncounterwithEpisodeOfCare enrichEncounterwithEpisode = new EnrichEncounterwithEpisodeOfCare(); 
    	
    	EnrichAppointmentwithPatient enrichAppointmentwithPatient = new EnrichAppointmentwithPatient();
    	EnrichAppointmentwithPractitioner enrichAppointmentwithPractitioner = new EnrichAppointmentwithPractitioner();
    	EnrichAppointmentwithLocation enrichAppointmentwithLocation = new EnrichAppointmentwithLocation();
    	EnrichAppointmentwithAppointment enrichAppointmentwithAppointment = new EnrichAppointmentwithAppointment();
    	
    	EnrichConsultantwithOrganisation consultantEnrichwithOrganisation = new EnrichConsultantwithOrganisation();
    	
    	//httpOutcomeProcessor httpOutcomeProcessor = new httpOutcomeProcessor();
    	
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
    	
    	from("hl7MinaListener")
    		.routeId("HL7v2")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT"))
					.wireTap("activemq:ADT")
					.end()
				.when(header("CamelHL7MessageType").isEqualTo("MFN"))
					.wireTap("activemq:MFN")
					.end()
			.end()
    		.transform(ack());
    	/*
    	 * 
    	 * //.when(header("CamelHL7MessageType").isEqualTo("ORM")).to("vm:ORM")
				//.when(header("CamelHL7MessageType").isEqualTo("ORU")).to("vm:ORU")
    	 * 
    	from("vm:ORM")
    		.routeId("ORM")
    		.log("ORM");

    	from("vm:ORU")
			.routeId("ORU")
			.log("ORU");
		*/
    	
    	from("activemq:MFN")
    		.routeId("MFN")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M02")).to("activemq:MFN_M02")
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M05")).to("activemq:MFN_M05")
    		.end();
    	
    	from("activemq:MFN_M02")
			.routeId("MFN_M02 Consultants")
			.process(mfnm02PractitionerProcessor)
			.log("Org = ${header.FHIROrganisationCode}")
			.enrich("vm:lookupOrganisation",consultantEnrichwithOrganisation)
	    	.enrich("vm:lookupResource",enrichUpdateType)
			.to("activemq:HAPIFHIR");
	
		from("activemq:MFN_M05")
			.routeId("MFN_M05 Clinic Locations")
			.process(mfnm05LocationProcessor)
			.enrich("vm:lookupOrganisation",enrichLocationwithOrganisation)
	    	.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichLocationwithLocation)
			.end()
	    	.enrich("vm:lookupResource",enrichUpdateType)
			.to("activemq:HAPIFHIR");
	    	
    	from("activemq:ADT")
    		.routeId("ADT")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A01")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A02")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A03")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to("activemq:ADT_A05A38")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A08")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A11")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A12")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A13")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A38")).to("activemq:ADT_A05A38") 
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("activemq:ADT_A28A31")
			/*	.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40") */
			.end();
    	
    	
    	// Demographics 
		from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31 Demographics")
			.process(adta28a31toPatient)
			.enrich("vm:lookupOrganisation",enrichPatientwithOrganisation)
			.enrich("vm:lookupGP",enrichPatientwithPractitioner)
			.enrich("vm:lookupPatient",enrichPatientwithPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
			//.to("activemq:FileFHIR");
		
    	// Encounters and Episodes
		from("activemq:ADT_A01A04A08")
			.routeId("ADT_A01A04A08 Encounters")
			.process(adta01a04a08toEncounter)
			.enrich("vm:lookupPatient",enrichEncounterwithPatient)
			.enrich("vm:lookupConsultant",enrichEncounterwithPractitioner)
			.enrich("vm:lookupOrganisation",enrichEncounterwithOrganisation)
			.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichEncounterwithLocation)
			.end()
			.choice()
			.when(header("FHIRAppointment").isNotNull())
				.enrich("vm:lookupAppointment",enrichEncounterwithAppointment)
			.end()
			// Episode lookup comes towards the end as it will use previous to simplfy the create/post lookup
			.enrich("vm:lookupEpisode",enrichEncounterwithEpisode)
			.enrich("vm:lookupEncounter",enrichEncounterwithEncounter)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
		
		// Encounters and Episodes
		from("activemq:ADT_A05A38")
			.routeId("ADT_A05A38 Appointments")
			.process(adta05a38toAppointment)
			.enrich("vm:lookupPatient",enrichAppointmentwithPatient)
			.enrich("vm:lookupConsultant",enrichAppointmentwithPractitioner)
			.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichAppointmentwithLocation)
			.end()
			.enrich("vm:lookupAppointment",enrichAppointmentwithAppointment)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
 
    	from("vm:lookupLocation")
    		.routeId("Loookup FHIR Location")
    		.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Location",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+TrustFHIRSystems.geturiNHSOrgLocation()+"|${header.FHIRLocation}",String.class))
	    	.to("vm:HAPIFHIR");
    	    	
    	from("vm:lookupOrganisation")
	    	.routeId("Lookup FHIR Organisation")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Organization",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"|${header.FHIROrganisationCode}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupGP")
    		.routeId("Lookup FHIR Practitioner (GP)")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_GMP_CODE+"|${header.FHIRGP}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupConsultant")
			.routeId("Lookup FHIR Practitioner (Consultant)")
	    	.setBody(simple(""))
	    	//.log("GET /Practitioner?identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}")
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}",String.class))
	    	.to("vm:HAPIFHIR");
	    	
    	from("vm:lookupPatient")
			.routeId("Lookup FHIR Patient")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier=${header.FHIRPatient}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupEncounter")
			.routeId("Lookup FHIR Encounter")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Encounter",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+TrustFHIRSystems.geturiNHSOrgActivityId()+"|${header.FHIREncounter}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupEpisode")
			.routeId("Lookup FHIR Episode")
			// this line of code ensures Episode is present
			.process(encountertoEpisodeOfCare)
			.to("vm:HAPIFHIR")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/EpisodeOfCare",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+env.getProperty("ORG.TrustEpisodeOfCare")+"|${header.FHIREpisode}",String.class))
	    	.to("vm:HAPIFHIR");
		    
    	from("vm:lookupAppointment")
			.routeId("Lookup FHIR Appointment")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Appointment",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+TrustFHIRSystems.geturiNHSOrgAppointmentId()+"|${header.FHIRAppointment}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	 from("vm:lookupResource")
	    	.routeId("Lookup FHIR Resources")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("${header.FHIRResource}",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("${header.FHIRQuery}",String.class))
	    	.to("vm:HAPIFHIR");
	    
    	from("activemq:FileFHIR")
			.routeId("FileStore")
			.to(env.getProperty("HAPIFHIR.FileStore")+"${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
		
    	from("vm:HAPIFHIR")
			.routeId("HAPI FHIR")
			.to(env.getProperty("HAPIFHIR.ServerNoExceptions"))
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.hl7v2.hapi.vm.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
			.end();
    	
    	from("activemq:HAPIFHIR")
			.routeId("HAPI FHIR MQ")
			.onException(org.apache.camel.http.common.HttpOperationFailedException.class).maximumRedeliveries(3).end()
			.to(env.getProperty("HAPIFHIR.Server"))
			.choice()
				.when(simple("${in.header.CamelHttpResponseCode} == 500"))
					.to("log:uk.co.mayfieldis.hl7v2.hapi.activemq.HAPIFHIR?showAll=true&multiline=true&level=ERROR")
					.throwException(org.apache.camel.http.common.HttpOperationFailedException.class,"Error Code 500")
			.end();
    	
    }
}
