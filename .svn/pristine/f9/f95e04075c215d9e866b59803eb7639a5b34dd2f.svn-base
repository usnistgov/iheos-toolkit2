package gov.nist.toolkit.sitemanagement.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import org.junit.Before;
import org.junit.Test;

public class SiteBuilderTest {
	static String workdir = "/Users/bill/tmp/sitetest";
	Site site;
	
	@Before
	public void setup() {
//		File work = new File(workdir);
//		File[] files = work.listFiles();
//		if (files != null)
//			for (File file : files) {
//				file.delete();
//			}
//		work.delete();
//		work.mkdir();
		
		site = new Site("mysite");
	}
	
	@Test
	public void testSiteName() {
		assertTrue("mysite".equals(site.getSiteName()));
		assertTrue(site.validate());
	}

	@Test
	public void testSimpleSiteConstruction() {
		String transName = "sq.b";
		String endpoint = "http://foo.bar";
		boolean isSecure = true;
		boolean isAsync = false;
		
		site.addTransaction(transName, endpoint, isSecure, isAsync);
		
		assertTrue(site.size() == 1);
		assertTrue(site.hasTransaction(TransactionType.STORED_QUERY));
		assertTrue(site.hasActor(ActorType.REGISTRY));
		assertTrue(site.validate());
	}

	@Test
	public void testRepositorySiteConstruction() {
		String uid="1.1.1";
		RepositoryType type = RepositoryType.REPOSITORY;
		String endpoint = "http://bar";
		boolean isSecure = false;
		boolean isAsync = true;
		site.addRepository(uid, type, endpoint, isSecure, isAsync);

		assertTrue(site.size() == 1);
		assertTrue(site.hasRepositoryB());
		try {
			assertTrue(uid.equals(site.getRepositoryUniqueId()));
			assertTrue(endpoint.equals(site.getRetrieveEndpoint(uid, isSecure, isAsync)));
		} catch (Exception e) { fail(); }
		assertTrue(site.validate());
	}

	@Test
	public void testSecInsecRepositorySiteConstruction() {
		String uid="1.1.1";
		RepositoryType type = RepositoryType.REPOSITORY;
		String endpoint = "http://bar";
		boolean isSecure = false;
		boolean isAsync = false;
		site.addRepository(uid, type, endpoint, false, isAsync);
		site.addRepository(uid, type, endpoint, true, isAsync);

		assertTrue(site.size() == 2);
		assertTrue(site.hasRepositoryB());
		assertTrue(site.repositoryUniqueIds().size() == 1);
		try {
			assertTrue(uid.equals(site.getRepositoryUniqueId()));
			assertTrue(endpoint.equals(site.getRetrieveEndpoint(uid, isSecure, isAsync)));
		} catch (Exception e) { fail(); }
		assertTrue(site.validate());
	}


}
