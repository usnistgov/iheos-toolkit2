package gov.nist.toolkit.repository.simple.index;



import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.simple.SimpleAsset;
import gov.nist.toolkit.repository.simple.index.db.DbContext;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

import java.util.ArrayList;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class IndexableAsset extends SimpleAsset {

	public IndexableAsset() throws RepositoryException {
		super();
	}


	@Override
	public void flush() throws RepositoryException {
		super.flush();		
		
		// Index relevant asset properties only if this asset belongs to an indexable repos
		// System.out.println("this.isIndexable(): "+this.isIndexable());
		if (this.isIndexable()) {
			DbIndexContainer dbc = new DbIndexContainer();

			try {
				
				// get indexable properties based on type
				// get the property value using getProperty
				
				ArrayList<String> properties = DbIndexContainer.getIndexableProperties();
				
				for (String property : properties) {
					
					String toBeIndexedValue = this.getProperty(property);
						
					if (DbContext.isDebugMode()) {
						System.out.println("Potential indexable asset property: "+ property);
					}
					
						if (toBeIndexedValue!=null && !"".equals(toBeIndexedValue)) {
												
							// Index only if the property is found in the Properties object
							String assetType = this.getAssetType().getKeyword();
							dbc.updateIndex(this.getRepository().getIdString(), this.getId().getIdString(),assetType, DbIndexContainer.getDbIndexedColumn(assetType, property), toBeIndexedValue);							
						} 
						else if (DbContext.isDebugMode()) { 		
							System.out.println("Asset: could not find value - not indexed: "+  property+ " - flush indexer value: "+ toBeIndexedValue);
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		
	}


	@Override
	public void removeAsset(Id assetId, boolean includeChildren)
			throws RepositoryException {
		// TODO remove from index
		super.removeAsset(assetId, includeChildren);
	}

	@Override
	public void deleteAsset() throws RepositoryException {
		// TODO remove from index
		super.deleteAsset();
	}

}
