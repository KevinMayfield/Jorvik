package uk.co.mayfieldis.camelRoute.UKFhirHapi;

import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipFileDataFormat;
import org.apache.camel.model.dataformat.BindyType;

import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.EnrichConsultantwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithLocation;
import uk.co.mayfieldis.jorvik.core.EnrichLocationwithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichResourcewithOrganisation;
import uk.co.mayfieldis.jorvik.core.EnrichwithUpdateType;

public class UKFHIRCamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	
    	ZipFileDataFormat zipFile = new ZipFileDataFormat();
    	zipFile.setUsingIterator(true);
    	
    	EnrichLocationwithLocation enrichLocationwithLocation = new EnrichLocationwithLocation();
    	EnrichLocationwithOrganisation enrichLocationwithOrganisation = new EnrichLocationwithOrganisation();
    	EnrichResourcewithOrganisation enrichOrg = new EnrichResourcewithOrganisation();
    	EnrichwithUpdateType enrichUpdateType = new EnrichwithUpdateType();
    	EnrichConsultantwithOrganisation consultantEnrichwithOrganisation = new EnrichConsultantwithOrganisation();
    	
    	errorHandler(deadLetterChannel("direct:error")
        		.maximumRedeliveries(2));
            	    
    	    from("direct:error")
            	.routeId("NHS SDS Fail Handler")
            	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute?level=ERROR&showAll=true");
    	
    			    
    	    from("vm:HAPIFHIR")
			.routeId("HAPI FHIR")
			.to("http:localhost:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
    	
	    	from("activemq:HAPIFHIR")
				.routeId("HAPI FHIR MQ")
				.to("http:localhost:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
	    	    
    	    from("vm:FileFHIR")
    			.routeId("FileStore")
    			.to("file:C:/NHSSDS/fhir?fileName=${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
    }
}
