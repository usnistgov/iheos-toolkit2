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

public class SimpleRemoveAssetTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");
	static Repository repos;
	static Asset a;

	// Create temp folder to be the External Cache
	//	@Rule
	//	public TemporaryFolder tempFolder = new TemporaryFolder()

	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);

		RepositoryFactory fact = new RepositoryFactory();
		repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple"));

		a = repos.createAsset("My Deletable Site", "This is my site", new SimpleType("site"));
		a.updateContent("My Content".getBytes());
	}

	@Test
	public void removeAssetTest() {
		Asset a2 = null;
		try {
			a2 = repos.getAsset(a.getId());
		} catch (RepositoryException e) {
			fail("Failed to load newly created asset");
		}
		assertNotNull(a2);

		try {
			repos.deleteAsset(a.getId());
		} catch (RepositoryException e) {
			fail(ExceptionUtil.exception_details(e));
		}

		a2 = null;
		try {
			a2 = repos.getAsset(a.getId());
			fail("Deleted asset still is loadable");
		} catch (RepositoryException e) {
		}
		assertNull(a2);

		try {
			repos.deleteAsset(a.getId());
			fail("Delete asset should have thrown an exception");
		} catch (RepositoryException e) {
		}
	}

}
