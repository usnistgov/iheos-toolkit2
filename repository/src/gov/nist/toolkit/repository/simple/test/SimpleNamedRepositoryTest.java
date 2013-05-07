package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleType;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleNamedRepositoryTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");
	static Id repId = null;
	static Repository repos;
	
	// Create temp folder to be the External Cache
//	@Rule
//	public TemporaryFolder tempFolder = new TemporaryFolder()
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
		repos = new RepositoryFactory().createNamedRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple", ""),
				"sites");
		repId = repos.getId();
	}
	
	@Test
	public void loadRepositoryTest() throws RepositoryException {
		RepositoryFactory repFact = new RepositoryFactory();
		repFact.getRepository(repId);
	}
	
	@Test
	public void assetTest() throws RepositoryException {
		Asset a = repos.createNamedAsset("My Site", "This is my site", new SimpleType("site"), "mysite");
		Id assetId = a.getId();
		
		Asset a2 = repos.getAsset(assetId);
		
		Id assetId2 = a2.getId();
		
		assertTrue("created and retrieved asset id should be the same", assetId.isEqual(assetId2));

	}

}
