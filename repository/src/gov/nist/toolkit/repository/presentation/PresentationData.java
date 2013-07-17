package gov.nist.toolkit.repository.presentation;

import gov.nist.toolkit.repository.simple.search.client.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.RepositoryIterator;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;
import gov.nist.toolkit.repository.simple.search.SearchResultIterator;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;

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
	
	public static List<String> getIndexablePropertyNames() {
		return DbIndexContainer.getIndexableProperties();
	}
	
	public static List<Asset> search(String[] repos, SearchCriteria sc) {
		
		ArrayList<Asset> result = new ArrayList<Asset>();
		
		try {
		RepositoryFactory fact = new RepositoryFactory();		
		
		int reposCt = repos.length;
		Repository[] reposList = new Repository[reposCt];
		
		for (int cx=0; cx<reposCt; cx++) {
			reposList[cx] = fact.getRepository(new SimpleId(repos[cx]));
		}

		AssetIterator iter = null;
		
			iter = new SearchResultIterator(reposList, sc );
		
			int recordCt = 0;
			if (iter!=null && recordCt++ < 50) {// hard limit for now
				while (iter.hasNextAsset()) {
					gov.nist.toolkit.repository.api.Asset aSrc = iter.nextAsset();
					
					Asset aDst = new Asset();
			
					aDst.setRepId(aSrc.getRepository().getIdString());
					aDst.setAssetId(aSrc.getId().getIdString());
					aDst.setDescription(aSrc.getDescription());
					aDst.setDisplayName(aSrc.getDisplayName());
					
					result.add(aDst);
				}
			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}


