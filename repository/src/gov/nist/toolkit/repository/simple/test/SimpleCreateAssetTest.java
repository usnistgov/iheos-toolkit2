package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.*;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleType;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleCreateAssetTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");
	
	// Create temp folder to be the External Cache
//	@Rule
//	public TemporaryFolder tempFolder = new TemporaryFolder()
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
	}

	@Test
	public void createUniqueIdsTest() {
		try {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple"));
		
		Asset a = repos.createAsset("My Deletable Site", "This is my site", new SimpleType("site"));
		
		assertFalse(repos.getId().isEqual(a.getId()));
		
		} catch (RepositoryException e) {
			fail(ExceptionUtil.exception_details(e));
		}
	}
}
