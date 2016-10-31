package uk.nhs.leedsth.hapifhir.jpa;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public class ExampleServerIT {

	private static IGenericClient ourClient;
	private static final FhirContext ourCtx = FhirContext.forDstu3();
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ExampleServerIT.class);

	private static int ourPort;

	private static Server ourServer;
	private static String ourServerBase;

	@Test
	public void testCreateAndRead() throws IOException {
	

		
        Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem(new String("http://jorvik.fhir.nhs.uk/Patient"));
        patient.getIdentifier().get(0).setValue("00002");
        patient.addName().addFamily("Test");
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(AdministrativeGender.FEMALE);


		IIdType id = ourClient.create().resource(patient).execute().getId();
		
		Patient pt2 = ourClient.read().resource(Patient.class).withId(id.getIdPart()).execute();
		assertEquals("Test", pt2.getName().get(0).getFamily().get(0).getValue());
		
		DocumentReference docRef = new DocumentReference();
		docRef.addIdentifier()
			.setSystem("http://fhir.leedsth.nhs.uk/PPM/FileStoreId/")
			.setValue("999999");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		Date date = new Date();
		docRef.setCreated(date);
		docRef.setIndexed(date);
		
		docRef.setSubject(new Reference("http://fhir.leedsth.nhs.uk/PASNumber/Patient/12334567"));
		
		CodeableConcept typeCode = new CodeableConcept();
		typeCode.addCoding()
			.setCode("823691000000103")
			.setSystem("http://snomed.info/sct")
			.setDisplay("Clinical letter");
		docRef.setType(typeCode);
		
		CodeableConcept classCode = new CodeableConcept();
		classCode.addCoding()
			.setCode("892611000000105")
			.setSystem("http://snomed.info/sct")
			.setDisplay("Hepatology service");
		docRef.setClass_(classCode);
		
		docRef.setCustodian(new Reference("https://sds.proxy.nhs.uk/Organization/RR8"));
		
		DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
		Attachment attach = new Attachment();
		attach.setContentType("application/pdf");
		attach.setUrl("https://fhir.leedsth.nhs.uk/PPMDocumentStore/MagicBeans/1");
		attach.setTitle("Jack and the Beanstalk");
		contentComponent.setAttachment(attach);
		docRef.addContent(contentComponent);
		
		DocumentReference.DocumentReferenceContextComponent contextComponent = new DocumentReference.DocumentReferenceContextComponent();
		CodeableConcept facility = new CodeableConcept();
		facility.addCoding()
			.setDisplay("Secondary care hospital")
			.setCode("46111000")
			.setSystem("http://snomed.info/sct");
		contextComponent.setFacilityType(facility);
		docRef.setContext(contextComponent);
		
		CodeableConcept security = docRef.addSecurityLabel();
		security.addCoding()
			.setCode("V")
			.setDisplay("very restricted")
			.setSystem("http://hl7.org/fhir/ValueSet/security-labels");
		
		
		id = ourClient.create().resource(docRef).execute().getId();
		
		DocumentReference docRef2 = ourClient.read().resource(DocumentReference.class).withId(id.getIdPart()).execute();
		// Move to master identifier
		assertEquals("823691000000103", docRef2.getType().getCoding().get(0).getCode());
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ourServer.stop();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		/*
		 * This runs under maven, and I'm not sure how else to figure out the target directory from code..
		 */
		
		String path = ExampleServerIT.class.getClassLoader().getResource(".keep_hapi-fhir-jpaserver-example").getPath();
		path = new File(path).getParent();
		path = new File(path).getParent();
		path = new File(path).getParent();
		
		/*
		String path = "/Development/GitHub/Jorvik/jorvik-hapi-fhir-jpaserver";
		*/
		ourLog.info("Project base path is: {}", path);

		ourPort = RandomServerPortProvider.findFreePort();
		ourServer = new Server(ourPort);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setDescriptor(path + "/src/main/webapp/WEB-INF/web.xml");
		webAppContext.setResourceBase(path + "/target/jorvik-hapi-fhir-stu3");
		webAppContext.setParentLoaderPriority(true);

		ourServer.setHandler(webAppContext);
		ourServer.start();

		ourCtx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		ourCtx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
		ourServerBase = "http://localhost:" + ourPort + "/baseStu3";
		ourClient = ourCtx.newRestfulGenericClient(ourServerBase);
		ourClient.registerInterceptor(new LoggingInterceptor(true));

	}

}
