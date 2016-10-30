package uk.nhs.jorvik.dao;


import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;

import ca.uhn.fhir.rest.method.RequestDetails;

//import java.util.List;

//import javax.persistence.criteria.CriteriaBuilder;



public class DocumentReferenceDAO extends BaseDAO<DocumentReference>
implements IDocumentReferenceDAO {
	
	public DocumentReferenceDAO() {
	    super(DocumentReference.class);
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
	public DocumentReference create(DocumentReference theResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentReference  read(IdType theId) {
		// TODO Auto-generated method stub
		return null;
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
