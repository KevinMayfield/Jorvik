package uk.nhs.jorvik.provider;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Required;


import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.jorvik.dao.IBaseDAO;

public abstract class BaseJPAResourceProvider<T extends IBaseResource> extends BaseJPAProvider implements IResourceProvider {
	
	
	private IBaseDAO<T> myDao;
	
	public BaseJPAResourceProvider() {
		// nothing
	}
	public BaseJPAResourceProvider(IBaseDAO<T> theDao) {
		myDao = theDao;
	}
	public IBaseDAO<T> getDao() {
		return myDao;
	}
	
	@Required
	public void setDao(IBaseDAO<T> theDao) {
		myDao = theDao;
	}
	/*
	public Parameters meta(@IdParam IdType theId, RequestDetails theRequestDetails) {
		Parameters parameters = new Parameters();
		Meta metaGetOperation = getDao().metaGetOperation(Meta.class, theId, theRequestDetails);
		parameters.addParameter().setName("return").setValue(metaGetOperation);
		return parameters;
	}
	*/
}
