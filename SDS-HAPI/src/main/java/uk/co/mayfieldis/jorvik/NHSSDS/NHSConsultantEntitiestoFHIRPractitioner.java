package uk.co.mayfieldis.jorvik.NHSSDS;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;
import org.hl7.fhir.instance.model.valuesets.PractitionerRole;

import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;


public class NHSConsultantEntitiestoFHIRPractitioner implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSConsultantEntities entity = exchange.getIn().getBody(NHSConsultantEntities.class);
		
		String Id = entity.PractitionerCode; 
		
		if ( Id.startsWith("C") && Id.length()>6)
		{
			Practitioner gp = new Practitioner();
			//gp.setId(entity.OrganisationCode);
			
			gp.addIdentifier()
				.setValue(entity.PractitionerCode)
				.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
			
			
			HumanName name = new HumanName();
			
			if (!entity.Surname.isEmpty()) 
			{
				name.addFamily(entity.Surname);
			}
			if (!entity.Initials.isEmpty()) 
			{
				name.addGiven(entity.Initials);
			}
			gp.setName(name);
			
			PractitionerPractitionerRoleComponent practitionerRole = new PractitionerPractitionerRoleComponent();
			
			CodeableConcept role= new CodeableConcept();
			role.addCoding()
					.setCode(PractitionerRole.DOCTOR.toString())
					.setSystem("http://hl7.org/fhir/practitioner-role");
			
			practitionerRole.setRole(role);
									
			CodeableConcept pracspecialty= new CodeableConcept();
			pracspecialty.addCoding()
				.setCode(entity.SpecialityFunctionCode)
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
			practitionerRole
				.addSpecialty(pracspecialty);
				
			gp.addPractitionerRole(practitionerRole);
			// XML as Ensemble doesn't like JSON
			String Response = ResourceSerialiser.serialise(gp, ParserType.XML);
			exchange.getIn().setHeader("FHIRResource","/Practitioner");
			exchange.getIn().setHeader("FHIRQuery","identifier="+gp.getIdentifier().get(0).getSystem()+"|"+gp.getIdentifier().get(0).getValue());
			exchange.getIn().setBody(Response);
		}
		
		exchange.getIn().setHeader("FHIROrganisationCode",entity.LocationOrganisationCode);
		// This is replace by line above
		//exchange.getIn().setHeader("ParentOrganisationCode",entity.LocationOrganisationCode);
	}

}
