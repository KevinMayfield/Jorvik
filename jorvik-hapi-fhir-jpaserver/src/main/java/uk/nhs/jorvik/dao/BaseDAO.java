package uk.nhs.jorvik.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceUnit;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract class BaseDAO<T extends IBaseResource> implements IBaseDAO<T> {
	
	@PersistenceUnit
	protected EntityManagerFactory emf;
	
	protected final Class<T> entityClass;
	
	
	
	protected BaseDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	/*
	public void setEntityManager(EntityManagerFactro em) {
		this.emf = em;
	}
	*/
	public T findById(long id) {
        return findById(id, LockModeType.NONE);
    }
	public T findById(long id, LockModeType lockModeType) {
        return emf.createEntityManager().find(entityClass, id, lockModeType);
    }
	
	
	/*
	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, IIdType theId, RequestDetails theRequestDetails) {
		 
		ActionRequestDetails requestDetails = new ActionRequestDetails(theRequestDetails, getResourceName(), theId);
		
		notifyInterceptors(RestOperationTypeEnum.META, requestDetails);

		Set<TagDefinition> tagDefs = new HashSet<TagDefinition>();
		BaseResource entity = readEntity(theId);
		for (BaseTag next : entity.getTags()) {
			tagDefs.add(next.getTag());
		}
		MT retVal = toMetaDt(theType, tagDefs);

		retVal.setLastUpdated(entity.getUpdatedDate());
		retVal.setVersionId(Long.toString(entity.getVersion()));
		
		return retVal;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, RequestDetails theRequestDetails) {
		// Notify interceptors
		ActionRequestDetails requestDetails = new ActionRequestDetails(theRequestDetails, getResourceName(), null);
		notifyInterceptors(RestOperationTypeEnum.META, requestDetails);

		String sql = "SELECT d FROM TagDefinition d WHERE d.myId IN (SELECT DISTINCT t.myTagId FROM ResourceTag t WHERE t.myResourceType = :res_type)";
		TypedQuery<TagDefinition> q = myEntityManager.createQuery(sql, TagDefinition.class);
		q.setParameter("res_type", myResourceName);
		List<TagDefinition> tagDefinitions = q.getResultList();

		MT retVal = toMetaDt(theType, tagDefinitions);

		return retVal;
		
	}
	*/

}
