package gov.nist.toolkit.repository.simple.index;



import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.simple.SimpleAsset;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

import java.util.ArrayList;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class IndexableAsset extends SimpleAsset {

	@Override
	public void flush() throws RepositoryException {
		super.flush();		
		
		// Index relevant asset properties only if this asset belongs to an indexable repos
		// System.out.println("this.isIndexable(): "+this.isIndexable());
		if (this.isIndexable()) {
			DbIndexContainer dbc = new DbIndexContainer();

			try {
				
				// get indexable proeperties based on type
				// get the property value using getProperty
				
				ArrayList<String> iap = DbIndexContainer.getIndexableProperties();
				
				for (String s : iap) {
					String propertyName = dbc.getDbColumnSuffix(s);
					String toBeIndexedValue = this.getProperty(propertyName);
						
					System.out.println("Indexable property: "+ propertyName);
					
						if (toBeIndexedValue!=null && !"".equals(toBeIndexedValue)) {
												
							// Index only if the property is found in the Properties object
							dbc.updateIndex(this.getRepository().getIdString(), this.getId().getIdString(), this.getAssetType().getKeyword(),propertyName,toBeIndexedValue);							
						} else { 		
							System.out.println("not indexable: "+  s+ " - flush indexer value: "+ toBeIndexedValue);
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		
	}

}
