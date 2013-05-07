package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.SimpleType;

import org.junit.Test;

public class SimpleTextAssetTest {
	
	@Test
	public void contentTest() throws RepositoryException {
		RepositoryFactory fact = new RepositoryFactory();
		Repository repos = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("site"));
		
		String myContent = "My Content";
		
		Asset a = repos.createAsset("My Site", "This is my site", new SimpleType("site"));
		a.updateContent(myContent, "text/plain");
		Id assetId = a.getId();
		
		Asset a2 = repos.getAsset(assetId);
		Id assetId2 = a2.getId();
		byte[] contentBytes = a2.getContent();
		String mimeType = a2.getMimeType();
		if (mimeType != null && mimeType.startsWith("text")) {
			String contentStr = new String(contentBytes);
			assertTrue(myContent.equals(contentStr));
		} else {
			fail("mimeType should be set and equal to test/plain");
		}
		
	}
}
