package gov.nist.toolkit.xdstools2.server.test.java.simulatorServiceManager;

import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimulatorServiceManagerConfigsTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	String sessionId = "sessionId1";
	Session session = new Session(warHome, SiteServiceManager.getSiteServiceManager(), sessionId);
	SimulatorServiceManager ssm = new SimulatorServiceManager(session);
	List<SimulatorConfig> sims = new ArrayList<SimulatorConfig>();
	SimManager sm = SimManager.get(session.getId());
	
	@Before
	public void setUp() {
		session.setEnvironment("NA2012");		
	}
	
	@After
	public void tearDown() {
		if (sims != null) {
			for (SimulatorConfig sc : sims) {
				session.deleteSim(sc.getId());
			}
			sims = null;
		}
	}
	
	public void createNewRegistry() {
		try {
			sims.addAll(ssm.getNewSimulator(ATFactory.ActorType.REGISTRY.getName()));
		} catch (Exception e) {
			Assert.fail(ExceptionUtil.exception_details(e));
		}
	}
	
	@Test
	public void simInSimConfigsTest() {
		int beforeSimsSize = sm.simConfigs().size();
		Assert.assertEquals(0, beforeSimsSize);
		createNewRegistry();
		Assert.assertEquals(1, sm.simConfigs().size());
		Assert.assertEquals(beforeSimsSize + 1, sm.simConfigs().size());
	}

}
