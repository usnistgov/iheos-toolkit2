package gov.nist.toolkit.session.test.java.server.services;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestKitTest {

	@Test
	public void simpleTest() {
		File warHome = new File("/Users/bmajur/workspace/toolkit/xdstools2/war");
		Session session = new Session(warHome, "sessionId1");
		SiteSpec siteSpec;
		String pid = "ba500592af78499^^^&1.19.6.24.109.42.1.3&ISO";
		String altpid = "8f06bfbc354c413^^^&1.3.6.1.4.1.21367.2005.3.7&ISO";
		session.setEnvironment("NA2013");
		siteSpec = new SiteSpec("pub", ActorType.REGISTRY, null);
		siteSpec.isSaml = false;
		siteSpec.isTls = false;

		String testName = "11990";
		List<String> sections = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("$patientid$", pid);
		params.put("$altpatientid$", altpid);
		Map<String, Object> params2 = null;
		boolean stopOnFirstFailure = true;
		List<Result> results;

		XdsTestServiceManager man = new XdsTestServiceManager(session);
		results = man.runMesaTest(session.getId(), siteSpec, testName, sections, params, params2, stopOnFirstFailure);
	}
}
