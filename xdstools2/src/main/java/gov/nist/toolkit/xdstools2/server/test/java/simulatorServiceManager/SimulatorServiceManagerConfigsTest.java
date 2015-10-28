package gov.nist.toolkit.xdstools2.server.test.java.simulatorServiceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SimulatorServiceManagerConfigsTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	String sessionId = "sessionId1";
	Session session = new Session(warHome, sessionId);
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
				SimCommon.deleteSim(sc.getId());
			}
			sim = null;
		}
	}
	
	public void createNewRegistry() {
		try {
			sim = ssm.getNewSimulator(ActorType.REGISTRY.getName(), null);
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
