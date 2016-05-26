package uk.co.mayfieldis.camelRoute.NHSSDSFHIR;

import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipFileDataFormat;
import org.apache.camel.model.dataformat.BindyType;

import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSConsultantEntities;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSConsultantEntitiestoFHIRPractitioner;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSEntities;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSEntitiestoFHIRResource;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSTrustLocationEntities;
import uk.co.mayfieldis.jorvik.NHSSDS.NHSTrustLocationEntitiestoFHIRLocation;
import uk.co.mayfieldis.jorvik.core.EnrichConsultantwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithLocation;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichResourcewithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichwithUpdateType;

public class SDSCamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	
    	ZipFileDataFormat zipFile = new ZipFileDataFormat();
    	zipFile.setUsingIterator(true);
    	
    	EnrichLocationwithLocation enrichLocationwithLocation = new EnrichLocationwithLocation();
    	EnrichLocationwithOrganisation enrichLocationwithOrganisation = new EnrichLocationwithOrganisation();
    	EnrichResourcewithOrganisation enrichOrg = new EnrichResourcewithOrganisation();
    	EnrichwithUpdateType enrichUpdateType = new EnrichwithUpdateType();
    	NHSConsultantEntitiestoFHIRPractitioner consultanttoFHIRPractitioner = new NHSConsultantEntitiestoFHIRPractitioner(); 
    	EnrichConsultantwithOrganisation consultantEnrichwithOrganisation = new EnrichConsultantwithOrganisation();
    	NHSTrustLocationEntitiestoFHIRLocation trustLocationEntitiestoFHIRLocation = new NHSTrustLocationEntitiestoFHIRLocation();
    	NHSEntitiestoFHIRResource nhsEntitiestoFHIRResource = new NHSEntitiestoFHIRResource();
    	
    	errorHandler(deadLetterChannel("direct:error")
        		.maximumRedeliveries(2));
            	    
    	    from("direct:error")
            	.routeId("NHS SDS Fail Handler")
            	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute?level=ERROR&showAll=true");
    	
    	
    		// Should follow Practice upload otherwise practice won't exist
    	    from("scheduler://egpcur?delay=24h")
    	    	.routeId("Retrieve NHS GP and Practice Amendments Zip")
    	    	.setHeader(Exchange.HTTP_METHOD, constant("GET"))
    	    	.to("http4://systems.hscic.gov.uk/data/ods/datadownloads/monthamend/current/egpam.zip")
    	    	.to("file:C:/NHSSDS/zip?fileName=${date:now:yyyyMMdd}-egpcur.zip");
    	  
    	    from("file:C:/NHSSDS/zip?readLock=markerFile&preMove=inprogress&move=.done&include=.*.(zip)&delay=1000")
	    		.routeId("Unzip NHS Reference Files")
	    		.unmarshal(zipFile)
	    		.split(body(Iterator.class))
	    			.streaming()
	    				.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.zip?level=INFO")
	    				.to("file:C:/NHSSDS/Extract")
	    			.end()
	    		.end();
    	    
    	    from("file:C:/NHSSDS/Extract?readLock=markerFile&preMove=inprogress&move=.done&include=.*.(csv)&delay=1000")
    	    	.routeId("Split CSV File")
    	    	.log("File ${header.CamelFileName}")
    	    	.choice()
    	    		.when(header(Exchange.FILE_NAME).isEqualTo("econcur.csv"))
    	    			.to("vm:ConsultantProcessing")
    	    		.when(header(Exchange.FILE_NAME).contains("location"))
    	    			.to("vm:LocationProcessing")	
    	    		.otherwise()
    					.to("vm:SDSProcessing")
    	    		.end();
    	        
    	    from("vm:ConsultantProcessing")
	    		.routeId("Process Consultant File")
	    		.log("Processing Consultant File")
	    		.unmarshal()
    			.bindy(BindyType.Csv, NHSConsultantEntities.class)
    			.split(body())
		    	.process(consultanttoFHIRPractitioner)
	    		.wireTap("activemq:Consultant")
	    		.end();
    	    
    	    from("vm:SDSProcessing")
    	    	.routeId("Prcess SDS/ODS File")
    	    	.log("Processing SDS/ODS File")
	    	    .unmarshal()
				.bindy(BindyType.Csv, NHSEntities.class)
				.split(body())
				.process(nhsEntitiestoFHIRResource)
				.wireTap("activemq:SDSResource")
				.end();
    	    
    	    from("vm:LocationProcessing")
	    		.routeId("Process Location File")
	    		.log("Processing Location File")
	    		.unmarshal()
				.bindy(BindyType.Csv, NHSTrustLocationEntities.class)
				.split(body())
				// Converts entity to FHIR
				.process(trustLocationEntitiestoFHIRLocation)
				.wireTap("activemq:Location")
				.end();
			
    	    from("activemq:Consultant")
		    	.routeId("FHIR Practitioner (Consultant)")
		    	.enrich("vm:lookupOrganisation",consultantEnrichwithOrganisation)
		    	.enrich("vm:lookupResource",enrichUpdateType)
		    	.filter(header(Exchange.HTTP_METHOD)
	    	    	.isEqualTo("POST"))
	    	    		.to("vm:Update")
	    	    	.end()
	    	    .filter(header(Exchange.HTTP_METHOD)
	    	    	.isEqualTo("PUT"))
	    	    		.to("vm:Update")
	    	    	.end();
	    	    
    	    from("activemq:Location")
		    	.routeId("FHIR Location")
		    	.enrich("vm:lookupOrganisation",enrichLocationwithOrganisation)
		    	.choice()
					.when(header("FHIRLocation").isNotNull())
						.enrich("vm:lookupLocation",enrichLocationwithLocation)
				.end()
		    	.enrich("vm:lookupResource",enrichUpdateType)
		    	.filter(header(Exchange.HTTP_METHOD)
	    	    	.isEqualTo("POST"))
	    	    		.to("vm:Update")
	    	    	.end()
	    	    .filter(header(Exchange.HTTP_METHOD)
	    	    	.isEqualTo("PUT"))
	    	    		.to("vm:Update")
	    	    		.to("vm:FileFHIR")
	    	    	.end();
	
    	    
    	    from("activemq:SDSResource")
    	    	.routeId("Process SDS Resource")
    	    	//.log("${header.FHIROrganisationCode}")
    	    	.enrich("vm:lookupOrganisation",enrichOrg)
    	    	.enrich("vm:lookupResource",enrichUpdateType)
    	    	.filter(header(Exchange.HTTP_METHOD)
    	    		.isEqualTo("POST"))
    	    		.to("vm:Update")
    	    	.end()
    	    	.filter(header(Exchange.HTTP_METHOD)
    	    		.isEqualTo("PUT"))
    	    		.to("vm:Update")
    	    	.end();
    	    	// Gets are discarded
    	    
    	    from("vm:Update")
    	    	.routeId("Update JPA Server")
    	    	.setHeader(Exchange.HTTP_PATH, simple("${header.FHIRResource}",String.class))
    	    	.setHeader(Exchange.HTTP_QUERY,simple("_format=xml",String.class))
		    	.log("Update type ${header.CamelHttpMethod} ${header.CamelHttpPath} ${header.CamelHttpQuery} Record Entity ID = ${header.OrganisationCode} partOf ${header.FHIROrganisationCode}")
		    	.setHeader("Prefer", simple("return=representation",String.class))
		    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute?level=INFO&showBody=true&showHeaders=true")
		    	.to("activemq:HAPIFHIR")
		    	.choice()
	    		.when(header(Exchange.FILE_NAME).isEqualTo("egpam.csv"))
	    			// only send updates for amendment load not a bulk load
	    			.to("http4:chft-tielive3.xthis.nhs.uk/REST/HAPI?connectionsPerRoute=60");
	    		
    	    	
    	    from("vm:lookupOrganisation")
    	    	.routeId("Lookup FHIR Organisation")
    	    	.setBody(simple(""))
    	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
    	    	.setHeader(Exchange.HTTP_PATH, simple("/Organization",String.class))
		    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"|${header.FHIROrganisationCode}",String.class))
		    	.to("vm:HAPIFHIR");
    	    
    	    from("vm:lookupLocation")
		    	.routeId("Lookup FHIR Location")
		    	//.log("Lookup Location ${header.FHIRLocation}")
		    	.setBody(simple(""))
		    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
		    	.setHeader(Exchange.HTTP_PATH, simple("/Location",String.class))
		    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTLocation+"|${header.FHIRLocation}",String.class))
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
			.to("http:localhost:8080/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
    	
	    	from("activemq:HAPIFHIR")
				.routeId("HAPI FHIR MQ")
				.to("http:localhost:8080/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
	    	    
    	    from("vm:FileFHIR")
    			.routeId("FileStore")
    			.to("file:C:/NHSSDS/fhir?fileName=${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
    }
}
