package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleType;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleAssetTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");
	
	// Create temp folder to be the External Cache
//	@Rule
//	public TemporaryFolder tempFolder = new TemporaryFolder()
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
	}

	@Test
	public void createAssetTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		Id assetId = a.getId();
		
		Asset a2 = repos.getAsset(assetId);
		
		Id assetId2 = a2.getId();
		
		assertTrue("created and retrieved asset id should be the same", assetId.isEqual(assetId2));
	}

	@Test
	public void contentTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		String myContent = "My Content";
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a.updateContent(myContent.getBytes());
		Id assetId = a.getId();
		
		Asset a2 = repos.getAsset(assetId);
		Id assetId2 = a2.getId();
		byte[] contentBytes = a2.getContent();
		
		assertNotNull(contentBytes);
		
		String myContent2 = new String(contentBytes);
		
		assertEquals("content does not match", myContent, myContent2);
		
		assertTrue("created and retrieved asset id should be the same", assetId.isEqual(assetId2));
	}
	
	@Test
	public void parentAssetTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a.updateContent("My Content".getBytes());
		Id assetId = a.getId();
		
		Asset a2 = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a2.updateContent("My Content".getBytes());
		Id assetId2 = a2.getId();
		
		a.addAsset(assetId2);  // make a the parent of a2
		
		assertFalse(assetId.isEqual(assetId2));
	}
	
	@Test
	public void getAssetTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a.updateContent("My Content".getBytes());
		Id assetId = a.getId();

		Asset a2 = repos.getAsset(assetId);
		
		assertNotNull(a2);
		
		assertTrue(assetId.isEqual(a2.getId()));

	}

	@Test
	public void assetIteratorTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a.updateContent("My Content".getBytes());
		
		Asset a2 = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a2.updateContent("My Content".getBytes());

		AssetIterator ai = repos.getAssets();
		
		assertTrue(ai.hasNextAsset());
		Asset b1 = ai.nextAsset();
		assertNotNull(b1);
		
		assertTrue(ai.hasNextAsset());
		Asset b2 = ai.nextAsset();
		assertNotNull(b2);
		
		assertFalse(ai.hasNextAsset());

	}

}
