package uk.nhs.jorvik.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import uk.nhs.jorvik.entity.DocumentReferenceEntity;
import uk.nhs.jorvik.entity.DocumentReferenceIdentifier;
import uk.nhs.jorvik.entity.PatientEntity;
import uk.nhs.jorvik.entity.PatientIdentifier;

//import java.util.List;

//import javax.persistence.criteria.CriteriaBuilder;



public class DocumentReferenceDAO extends BaseDAO<DocumentReference>
implements IDocumentReferenceDAO {
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.jorvik.provider.DocumentReferenceResourceProvider.class);
	
	public DocumentReferenceDAO() {
	    super(DocumentReference.class);
	}
	
	public DocumentReferenceDAO(EntityManagerFactory entityManagerFactory) {
		super(DocumentReference.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<DocumentReference> getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, IIdType theId,
			RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentReference create(DocumentReference theDocRef) {
		log.info("called DocumentReferenceDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager");
			em.getTransaction().begin();
			
			log.info("Call persist");
			DocumentReferenceEntity edr = new DocumentReferenceEntity();
			edr.setTypeSnmdCT(theDocRef.getType().getCoding().get(0).getCode());
			edr.setTypeSnmdCTName(theDocRef.getType().getCoding().get(0).getDisplay());
			edr.setClassSnmdCT(theDocRef.getClass_().getCoding().get(0).getCode());
			edr.setClassSnmdCTName(theDocRef.getClass_().getCoding().get(0).getDisplay());
			
			edr.setPASPatient(theDocRef.getSubject().getReference().replace("http://fhir.leedsth.nhs.uk/PASNumber/Patient/",""));
			log.info("Built DocumentReference entity");
			
			edr.setCreated(theDocRef.getCreated());
			edr.setIndexed(theDocRef.getIndexed());
			edr.setAttachmentContentType(theDocRef.getContent().get(0).getAttachment().getContentType());
			edr.setAttachmentUri(theDocRef.getContent().get(0).getAttachment().getUrl());
			edr.setAttachmentTitle(theDocRef.getContent().get(0).getAttachment().getTitle());
			
			em.persist(edr);
		
			List<DocumentReferenceIdentifier> drids = new ArrayList<DocumentReferenceIdentifier>();
			DocumentReferenceIdentifier dri = new DocumentReferenceIdentifier(edr);
			dri.setSystem(theDocRef.getIdentifier().get(0).getSystem());
			dri.setValue(theDocRef.getIdentifier().get(0).getValue());
			edr.setIdentifiers(drids);
			    
			em.persist(dri);
			
			em.getTransaction().commit();
			
			log.info("Called it PERSIST id="+edr.getId().toString());
			theDocRef.setId(edr.getId().toString());
			
			em.close();
			
			log.debug("Finished call to persist DocumentReference");
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
		}
		
		log.info("In the finally");
		return theDocRef;	
	}

	@Override
	public DocumentReference  read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		DocumentReference docRef  = null;
		try
		{
		
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager DocumentReference.read");
			//em.getTransaction().begin();
			
			DocumentReferenceEntity edr = (DocumentReferenceEntity) em.find(DocumentReferenceEntity.class,Integer.parseInt(theId.getIdPart()));
			
			docRef = new DocumentReference();
			
			for(int f=0;f<edr.getIdentifiers().size();f++)
			{
				docRef.addIdentifier()
					.setSystem(edr.getIdentifiers().get(f).getSystem())
					.setValue(edr.getIdentifiers().get(f).getValue());
			}
			docRef.setId(edr.getId().toString());
			
			CodeableConcept typeCode = new CodeableConcept();
			typeCode.addCoding()
				.setCode(edr.getTypeSnmdCT())
				.setDisplay(edr.getTypeSnmdCTName())
				.setSystem("http://snomed.info/sct");
				
			docRef.setType(typeCode);
			
			CodeableConcept classCode = new CodeableConcept();
			classCode.addCoding()
				.setCode(edr.getClassSnmdCT())
				.setDisplay(edr.getClassSnmdCTName())
				.setSystem("http://snomed.info/sct");
				
			docRef.setClass_(classCode);
			docRef.setSubject(new Reference("http://fhir.leedsth.nhs.uk/PASNumber/Patient/"+edr.getPASPatientId()));
	        
			docRef.setCreated(edr.getCreated());
			
			docRef.setIndexed(edr.getIndexed());
			
			DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
			Attachment attach = new Attachment();
			attach.setContentType(edr.getAttachmentContentType());
			attach.setUrl(edr.getAttachmentUri());
			attach.setTitle(edr.getAttachmentTitle());
			contentComponent.setAttachment(attach);
			docRef.addContent(contentComponent);
			
			em.close();
	        log.info("Built the DocumentReference");
	       
			return docRef;
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
			//log.error(ex.getStackTrace().toString());
		}
		finally
		{
			log.info("In the finally");
		}
		
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(docRef);
		return docRef;
	}

	

	
	
/*
@Override
public List<DocumentReferenceEntity> findAll(boolean withBids) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    
    CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
    Root<Item> i = criteria.from(Item.class);
    criteria.select(i)
        .distinct(true) // In-memory "distinct"!
        .orderBy(cb.asc(i.get("auctionEnd")));
    if (withBids)
        i.fetch("bids", JoinType.LEFT);
   
    return em.createQuery(criteria).getResultList();
}

@Override
public List<Item> findByName(String name, boolean substring) {
    return em.createNamedQuery(
        substring ? "getItemsByNameSubstring" : "getItemsByName"
    ).setParameter(
        "itemName",
        substring ? ("%" + name + "%") : name
    ).getResultList();
}

@Override
public List<ItemBidSummary> findItemBidSummaries() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ItemBidSummary> criteria =
        cb.createQuery(ItemBidSummary.class);
    Root<Item> i = criteria.from(Item.class);
    Join<Item, Bid> b = i.join("bids", JoinType.LEFT);
    criteria.select(
        cb.construct(
            ItemBidSummary.class,
            i.get("id"), i.get("name"), i.get("auctionEnd"),
            cb.max(b.<BigDecimal>get("amount"))
        )
    );
    criteria.orderBy(cb.asc(i.get("auctionEnd")));
    criteria.groupBy(i.get("id"), i.get("name"), i.get("auctionEnd"));
    return em.createQuery(criteria).getResultList();
}
*/
}
