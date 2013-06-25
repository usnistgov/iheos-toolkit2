package gov.nist.toolkit.repository.simple.index.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleType;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

import java.io.File;

import javax.servlet.ServletContext;

import org.junit.BeforeClass;
import org.junit.Test;
public class IndexableRepositoryTest {
	
	/*
	 * Important: The following system path variables need to verified manually before running the test.
	 * 
	 */
	static String RootPath = "/e/artrep_test_resources/"; 		// Root Path or the Test resources folder
	static String RepositoriesPath = RootPath + "repositories"; // Repositories folder
	static String InstallationPath = RootPath+"installation";	// Path containing the WEB-INF folder (for External_Cache)
	
	static File RootOfAllRepositories = new File(RepositoriesPath);
	static Installation inst = null;
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
		
		// The MockServletContext is used for testing purposes only
		
		ServletContext sc = MockServletContext.getServletContext(InstallationPath); 
		
		Installation.installation(sc);
		
		String externalCache = Installation.installation().propertyServiceManager()
									.getToolkitProperties().get("External_Cache");
		System.out.println(externalCache);
		Installation.installation().setExternalCache(new File(sc.getRealPath(externalCache)));
		inst = Installation.installation();
	}

	
	
	@Test
	public void getGlobalPropertyFlagTest() {
		
		try {
			System.out.println (inst.tkProps.get("toolkit.servlet.context", "xdstools2"));
			
			String indexingControl = inst.propertyServiceManager()
									.getToolkitProperties().get("Index_Method");
			
			assertEquals(indexingControl,"partial");
			
			
		} catch (Exception e) {
			 e.printStackTrace();
			 fail("Property error");
		}
	}
	
	
	@Test
	public void newSimpleRepositoryTest() throws RepositoryException {

		/*
		 * simpleRepos - This is a SIMPLE (non-indexable) repos type
		 * 
		 */
		
		Repository repos;
		
		RepositoryFactory fact = new RepositoryFactory();
		
		repos = fact.createRepository(
				"This is a simple repository",
				"Description - the most basic repository",
				new SimpleType("simpleRepos"));
		
		assertNotNull(repos);
		
		// This asset type is indexable but should not be indexed because the parent repos is defined as NOT indexable
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("siteAsset"));
		a.updateContent("basic string - text stream - content\ntest 1", "text/*");
		
		assertFalse(DbIndexContainer.isRepositoryIndexable(repos.getType()));		
		
		assertNotNull(a);
				
		DbIndexContainer dbc = new DbIndexContainer();
		String property = dbc.getIndexedPropertyByAssetId(a.getId().toString(), a.getAssetType().getKeyword(), "description");
		
		assertTrue("".equals(property));

		
	}
	
	
	@Test
	public void newIndexableRepositoryTest() throws RepositoryException {

		/*
		 * documentRepos - This is an indexable repos
		 * 
		 */
		
		Repository repos;
		
		RepositoryFactory fact = new RepositoryFactory();
		
		repos = fact.createRepository(
				"This is a document repository",
				"Description - indexable repository",
				new SimpleType("documentRepos"));
		
		assertNotNull(repos);
		
		// This asset type should be indexed because both the asset and parent repos are defined as indexable
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("siteAsset"));
		
		assertTrue(DbIndexContainer.isRepositoryIndexable(repos.getType()));
		assertNotNull(a);
				
		DbIndexContainer dbc = new DbIndexContainer();
		String indexedProperty = dbc.getIndexedPropertyByAssetId(a.getId().toString(), a.getAssetType().getKeyword(), "description");		
		assertTrue(!"".equals(indexedProperty));
		assertTrue("This is my site".equals(indexedProperty));
		

		a.setProperty("patientId", "bogus");	
		
		indexedProperty = dbc.getIndexedPropertyByAssetId(a.getId().toString(), a.getAssetType().getKeyword(), "patientId");		
		assertTrue("bogus".equals(indexedProperty));

	}


	//@Test
	public void purgeIndexTest() throws RepositoryException {
		System.out.println("Running purge...");
		
		DbIndexContainer dbc = new DbIndexContainer();

		try {
			if (dbc.doesIndexContainerExist()) {
				dbc.purge();
				assertTrue(dbc.getIndexCount()==0);
			}
		
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("purge failed");
						
		}
	}
	
	
	@Test
	public void reindexTest() {

		String indexMethod = inst.propertyServiceManager()
				.getToolkitProperties().get("Index_Method");
		
		try {
			if ("full".equalsIgnoreCase(indexMethod)) {
				DbIndexContainer dbc = new DbIndexContainer();
				dbc.reIndex();
			}			
		} catch (Exception e) {
			fail("ReIndex failed " +e.toString());
		}
		
		
	}	
	
}
