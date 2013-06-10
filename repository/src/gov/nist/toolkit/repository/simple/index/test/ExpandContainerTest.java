package gov.nist.toolkit.repository.simple.index.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.junit.BeforeClass;
import org.junit.Test;

public class ExpandContainerTest {

	/*
	 * Important: The following developer's system path variables need to verified manually before running the test.
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
	public void retrieveIndexPropertyTest() {		
		for (String s : DbIndexContainer.getIndexableProperties()) {
			System.out.println (s);
		}
		
	}
	
	@Test
	public void expandColumnsTest() {
		DbIndexContainer dbc = new DbIndexContainer();
		

		try {
			ArrayList<String> iap = DbIndexContainer.getIndexableProperties();
			dbc.expandContainer(iap.toArray(new String[iap.size()]));
		
			for (String s : iap) {
				assertTrue(dbc.isIndexed(s));	
			}
			
		} catch (Exception e) {
			fail("test expand failed!");
						
		}

	}
	
}
