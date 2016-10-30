package uk.nhs.jorvik.dao;


import ca.uhn.fhir.rest.method.RequestDetails;

import org.hl7.fhir.dstu3.model.IdType;

import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

// based on ca.uhn.fhir.jpa.daoIFhirResourceDao.java

public interface IBaseDAO<T extends IBaseResource>  {

   T findById(long id);
   
   Class<T> getResourceType();

   <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, RequestDetails theRequestDetails);
   <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, IIdType theId, RequestDetails theRequestDetails);

   T create(T theResource);

   T read(IdType theId);
   
  // Class<T> getResourceType();

  //  T findById(ID id, LockModeType lockModeType);

  //  T findReferenceById(ID id);

  //  List<T> findAll();

  //  Long getCount();

  //  T makePersistent(T entity);

   // void makeTransient(T entity);

  //  void checkVersion(T entity, boolean forceUpdate);

}