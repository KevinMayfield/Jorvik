package uk.co.mayfieldis.jorvik.NHSSDS;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerRoleComponent;
import org.hl7.fhir.dstu3.model.Reference;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;

import uk.co.mayfieldis.jorvik.core.FHIRConstants.FHIRCodeSystems;

// 8-Aug-2016 KGM Replaced lookup code with FHIR Transactions 

public class NHSEntitiestoFHIRResource implements Processor {

	public NHSEntitiestoFHIRResource(FhirContext ctx)
	{
		this.ctx = ctx;
	}
	private FhirContext ctx;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSEntities entity = (NHSEntities) exchange.getIn().getBody(NHSEntities.class);
		
		String Id = entity.OrganisationCode; 
		
		Organization parentOrg = new Organization();
		parentOrg.setId(IdDt.newRandomUuid());
		if (entity.ParentOrganisationCode != null && !entity.ParentOrganisationCode.isEmpty())
		{
			parentOrg.addIdentifier()
				.setValue(entity.ParentOrganisationCode)
				.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
		}
		else
		{
			parentOrg.addIdentifier()
				.setValue(entity.HighLevelHealthGeography)
				.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
		}
		
		if ( (Id.startsWith("G") || Id.startsWith("C")) && Id.length()>6)
		{
			Practitioner gp = new Practitioner();
			//gp.setId(entity.OrganisationCode);
			
			gp.addIdentifier()
				.setValue(entity.OrganisationCode)
				.setSystem(FHIRCodeSystems.URI_NHS_GMP_CODE);
			
			String[] names = entity.Name.split(" ");
			HumanName name = new HumanName();
			
			if (names.length>0) 
			{
				name.addFamily(names[0]);
			}
			for (int f=1;f<names.length;f++)
			{
				if (names[f] !=null && !names[f].isEmpty())
				{
					name.addGiven(names[f]);
				}
			}
			gp.addName(name);
			
			Address address = gp.addAddress();
			
			if (entity.AddressLine1 != null && !entity.AddressLine1.isEmpty())
			{
				address.addLine(entity.AddressLine1);
			}
			if (entity.AddressLine2 != null && !entity.AddressLine2.isEmpty())
			{
				address.addLine(entity.AddressLine2);
			}
			if (entity.AddressLine3 != null && !entity.AddressLine3.isEmpty())
			{
				address.addLine(entity.AddressLine3);
			}
			if (entity.AddressLine4 != null && !entity.AddressLine4.isEmpty())
			{
				address.addLine(entity.AddressLine4);
			}
			if (entity.AddressLine5 != null && !entity.AddressLine5.isEmpty())
			{
				address.addLine(entity.AddressLine5);
			}
			if (entity.Postcode != null && !entity.Postcode.isEmpty())
			{
				address.setPostalCode(entity.Postcode);
			}
			
			if (entity.StatusCode.equals("A"))
			{
				// This setting looks to be garbage. Believe active means they are still on the register but may not be practising medicine 
				gp.setActive(true);
			}
			else
			{
				gp.setActive(false);
			}
			
			if (entity.ContactTelephoneNumber != null && !entity.ContactTelephoneNumber.isEmpty())
			{
				gp.addTelecom()
					.setValue(entity.ContactTelephoneNumber)
					.setSystem(ContactPointSystem.PHONE)
					.setUse(ContactPointUse.WORK);
			}
			
			//PractitionerRoleComponent practitionerRole = new PractitionerRoleComponent();
			
			PractitionerRoleComponent practitionerRole = gp.addRole();
									
			CodeableConcept pracspecialty= new CodeableConcept();
			pracspecialty.addCoding()
				.setCode("600")
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
			practitionerRole
				.addSpecialty(pracspecialty);
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			Period period = new Period();
			
			if (entity.LeftParentDate!=null && !entity.LeftParentDate.isEmpty())
			{
				try {
					period.setEnd(fmt.parse(entity.LeftParentDate));
					gp.setActive(false);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			if (entity.JoinParentDate!=null && !entity.JoinParentDate.isEmpty())
			{
				try {
					period.setStart(fmt.parse(entity.JoinParentDate));
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			practitionerRole.setPeriod(period);
			
			
			if (entity.OpenDate != null && !entity.OpenDate.isEmpty())
			{
				Extension activePeriod = new Extension();
				fmt = new SimpleDateFormat("yyyyMMdd");
				
				period = new Period();
				activePeriod.setUrl(FHIRCodeSystems.URI_NHS_ACTIVE_PERIOD);
				try {
					period.setStart(fmt.parse(entity.OpenDate));
					
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
				if (entity.CloseDate != null && !entity.CloseDate.isEmpty())
				{
					try {
						period.setEnd(fmt.parse(entity.CloseDate));
						
		        	} catch (ParseException e1) {
		        	// TODO Auto-generated catch block
		        	}
				}
				activePeriod.setValue(period);
				gp.addExtension(activePeriod);
			}
			
			CodeableConcept role= new CodeableConcept();
			role.addCoding()
					.setCode("doctor")
					.setSystem("http://hl7.org/fhir/practitioner-role");
			
			
			practitionerRole.setCode(role);
			practitionerRole.setOrganization(new Reference(parentOrg.getId()));
			
			
			
			Bundle bundle = new Bundle();
			
			bundle.setType(BundleType.TRANSACTION);
			
			bundle.addEntry()
			   .setFullUrl(parentOrg.getId())
			   .setResource(parentOrg)
			   .getRequest()
			      .setUrl("Organization")
			      .setIfNoneExist("Organization?identifier="+parentOrg.getIdentifier().get(0).getSystem()+"|"+parentOrg.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
		
			bundle.addEntry()
			   .setResource(gp)
			   .getRequest()
			      .setUrl("Practitioner?identifier="+gp.getIdentifier().get(0).getSystem()+"|"+gp.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.PUT);
			
			// XML as Ensemble doesn't like JSON
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
			
			exchange.getIn().setHeader("FHIRResource","/");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			exchange.getIn().setBody(Response);
			
		}
		else
		{
		
			Organization organisation = new Organization();
			
			organisation.setPartOf(new Reference(parentOrg.getId()));
			
			organisation.addIdentifier()
				.setValue(Id)
				.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
			
			organisation.setName(entity.Name);
			
			String FHIROrgType = null;
			String NHSOrgTypeDesc = null;
			String NHSOrgType = null;
			
			switch(exchange.getIn().getHeader(Exchange.FILE_NAME).toString().toUpperCase())
			{
				case "ECCG.CSV":
					NHSOrgType = "CC";
					NHSOrgTypeDesc= "Clinical Commissioning Group (CCG)";
					FHIROrgType = "team";
					break;
				case "EPRACCUR.CSV":
				case "EGPAM.CSV":
					NHSOrgType = "PR";
					NHSOrgTypeDesc= "GP Practices in England and Wales";
					FHIROrgType = "prov";
					break;
				case "ETR.CSV":
					NHSOrgType = "TR";
					NHSOrgTypeDesc= "NHS Trust";
					FHIROrgType = "prov";
					break;
				case "ETS.CSV":
					NHSOrgType = "SITE";
					NHSOrgTypeDesc= "NHS Trust Site";
					FHIROrgType = "prov";
					break;
				case "EAUTH.CSV":
					NHSOrgType = "HA";
					NHSOrgTypeDesc= "NHS (England) High Level Health Geography";
					FHIROrgType = "govt";
					break;
			}
			
			if (FHIROrgType != null)
			{
				CodeableConcept type=new CodeableConcept();
				type.addCoding()
					.setSystem("http://hl7.org/fhir/organization-type")
					.setCode(FHIROrgType);
				organisation.setType(type);
			}
			
			if (NHSOrgType != null)
			{
				
				CodeableConcept type=new CodeableConcept();
				type.addCoding()
					.setSystem(FHIRCodeSystems.URI_NHS_ORGANISATION_TYPE)
					.setCode(NHSOrgType)
					.setDisplay(NHSOrgTypeDesc);
				
				Extension extNHSOrg = new Extension();
				extNHSOrg
					.setUrl(FHIRCodeSystems.URI_NHS_ORGANISATION_TYPE)
					.setValue(type);
				organisation.addExtension(extNHSOrg);
			}
			
			Address address = organisation.addAddress();
			
			if (entity.AddressLine1 != null && !entity.AddressLine1.isEmpty())
			{
				address.addLine(entity.AddressLine1);
			}
			if (entity.AddressLine2 != null && !entity.AddressLine2.isEmpty())
			{
				address.addLine(entity.AddressLine2);
			}
			if (entity.AddressLine3 != null && !entity.AddressLine3.isEmpty())
			{
				address.addLine(entity.AddressLine3);
			}
			if (entity.AddressLine4 != null && !entity.AddressLine4.isEmpty())
			{
				address.addLine(entity.AddressLine4);
			}
			if (entity.AddressLine5 != null && !entity.AddressLine5.isEmpty())
			{
				address.addLine(entity.AddressLine5);
			}
			if (entity.Postcode != null && !entity.Postcode.isEmpty())
			{
				address.setPostalCode(entity.Postcode);
			}
			if (entity.ContactTelephoneNumber != null && !entity.ContactTelephoneNumber.isEmpty())
			{
				organisation.addTelecom()
					.setValue(entity.ContactTelephoneNumber)
					.setSystem(ContactPointSystem.PHONE)
					.setUse(ContactPointUse.WORK);
			}
			if (entity.StatusCode != null)
			{
				if (entity.StatusCode.equals("A"))
				{
					// This setting looks to be garbage. Believe active means they are still on the register but may not be practising medicine 
					organisation.setActive(true);
				}
				else
				{
					organisation.setActive(false);
				}
			}
			if (entity.OpenDate != null && !entity.OpenDate.isEmpty())
			{
				Extension activePeriod = new Extension();
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
				Period period = new Period();
				activePeriod.setUrl(FHIRCodeSystems.URI_NHS_ACTIVE_PERIOD);
				try {
					period.setStart(fmt.parse(entity.OpenDate));
					organisation.setActive(true);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
				if (entity.CloseDate != null && !entity.CloseDate.isEmpty())
				{
					try {
						period.setEnd(fmt.parse(entity.CloseDate));
						organisation.setActive(false);
		        	} catch (ParseException e1) {
		        	// TODO Auto-generated catch block
		        	}
				}
				activePeriod.setValue(period);
				organisation.addExtension(activePeriod);
			}
			
			Bundle bundle = new Bundle();
			bundle.setType(BundleType.TRANSACTION);
			
			bundle.addEntry()
			   .setFullUrl(parentOrg.getId())
			   .setResource(parentOrg)
			   .getRequest()
			      .setUrl("Organization")
			      .setIfNoneExist("Organization?identifier="+parentOrg.getIdentifier().get(0).getSystem()+"|"+parentOrg.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.POST);
			 
			bundle.addEntry()
			   .setResource(organisation)
			   .getRequest()
			      .setUrl("Organization?identifier="+organisation.getIdentifier().get(0).getSystem()+"|"+organisation.getIdentifier().get(0).getValue())
			      .setMethod(HTTPVerb.PUT);
			
			String Response = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
			
			exchange.getIn().setHeader("FHIRResource","/");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			exchange.getIn().setBody(Response);
			
	
		}
		
		
	}

}
