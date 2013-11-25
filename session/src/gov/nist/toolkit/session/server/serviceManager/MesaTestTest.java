package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MesaTestTest {
	String mesaTestSession = "bill";
	SiteSpec siteSpec = new SiteSpec("pub", ActorType.REGISTRY, null);
	Map<String, Object> params2 = new HashMap<String, Object>();
	boolean stopOnFirstFailure = true;
	String sessionId = "MySession";
	Session session = new Session(new File("/Users/bmajur/workspace/toolkit/xdstools2/war"), SiteServiceManager.getSiteServiceManager(), sessionId);
	EnvSetting es = new EnvSetting(sessionId, "NA2012");
	List<String> sections = new ArrayList<String>();
	Map<String, String> params = new HashMap<String, String>();

//	@Test
	public void buildTestData() {
		siteSpec.isTls = false;
		String testName = "12346";
		params.put("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");		
		new XdsTestServiceManager(session).runMesaTest(mesaTestSession, siteSpec, testName, sections, params, params2, stopOnFirstFailure);
	}

//	@Test
	public void verifyTestData() {
		String testName = "11901";
		siteSpec.isTls = false;
		params.put("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");		
		new XdsTestServiceManager(session).runMesaTest(mesaTestSession, siteSpec, testName, sections, params, params2, stopOnFirstFailure);
	}

	@Test
	public void twoStepTest() {
		String testName = "11966";
		siteSpec.isTls = false;
		params.put("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");		
		new XdsTestServiceManager(session).runMesaTest(mesaTestSession, siteSpec, testName, sections, params, params2, stopOnFirstFailure);
	}
}
