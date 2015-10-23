package gov.nist.toolkit.session.test.java.server.services;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.services.FindDocuments;
import gov.nist.toolkit.xdsexception.XdsException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FindDocumentsTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	Session session = new Session(warHome, "sessionId1");
	FindDocuments fd;
	SiteSpec siteSpec;
	String pid = "1.2.3^^^1.2.3@ISO";
	
	@Before
	public void setUp() {
		session.setEnvironment("NA2012");
		try {
			fd = new FindDocuments(session);
		} catch (XdsException e) {
			Assert.fail(e.getMessage());
		}
		siteSpec = new SiteSpec("MySite", ActorType.REGISTRY, null);
		siteSpec.isSaml = true;
		siteSpec.isTls = true;
	}
	
	@Test
	public void test1() {
		List<Result> results = fd.run(siteSpec, pid, false);
	}
	
}
