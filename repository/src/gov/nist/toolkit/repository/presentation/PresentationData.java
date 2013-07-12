package gov.nist.toolkit.repository.presentation;

import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.RepositoryIterator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PresentationData implements IsSerializable, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4939311135239253727L;

	
	public Map<String, String> getRepositoryDisplayTags() {
		
		
		Map<String, String> m = new HashMap<String,String>();
		
		RepositoryIterator it;
		try {
			
			it = new RepositoryFactory().getRepositories();

			Repository r =  null;
			while (it.hasNextRepository()) {
				r = it.nextRepository();
				m.put(r.getId().getIdString(), r.getDisplayName());
				
			}
		} catch (RepositoryException e) {
			return null;
		}
		return m;
		
	}
	
}


