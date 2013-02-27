package gov.nist.toolkit.xdstools2.server.test.java.simulatorServiceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimulatorServiceManagerConfigsTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	String sessionId = "sessionId1";
	Session session = new Session(warHome, SiteServiceManager.getSiteServiceManager(), sessionId);
	SimulatorServiceManager ssm = new SimulatorServiceManager(session);
	Simulator sim = null;
	SimManager sm = new SimCache().getSimManagerForSession(session.getId());
	
	@Before
	public void setUp() {
		session.setEnvironment("NA2012");		
	}
	
	@After
	public void tearDown() {
		if (sim != null) {
			for (SimulatorConfig sc : sim.getConfigs()) {
				session.deleteSim(sc.getId());
			}
			sim = null;
		}
	}
	
	public void createNewRegistry() {
		try {
			sim = ssm.getNewSimulator(ATFactory.ActorType.REGISTRY.getName());
		} catch (Exception e) {
			Assert.fail(ExceptionUtil.exception_details(e));
		}
	}
	
	@Test
	public void simInSimConfigsTest() throws Exception {
		int beforeSimsSize = sm.getAllSites().size();
		Assert.assertEquals(0, beforeSimsSize);
		createNewRegistry();
		Assert.assertEquals(1, sm.getAllSites().size());
		Assert.assertEquals(beforeSimsSize + 1, sm.getAllSites().size());
	}

}
