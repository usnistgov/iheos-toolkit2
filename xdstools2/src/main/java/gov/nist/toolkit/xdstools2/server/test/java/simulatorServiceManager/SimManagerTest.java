package gov.nist.toolkit.xdstools2.server.test.java.simulatorServiceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SimManagerTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	Session session = new Session(warHome, "sessionId1");
	SimulatorServiceManager ssm = new SimulatorServiceManager(session);
	Simulator sim = null;
	Sites beforeSites;
	Sites afterSites;
	
	@Before
	public void setUp() {
		session.setEnvironment("NA2012");
		
		try {
			beforeSites = new SimCache().getSimManagerForSession(session.getId())
					.getAllSites();
		} catch (Exception e) {
			Assert.fail(ExceptionUtil.exception_details(e));
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
	public void newSimPresentinAllSites() {
		createNewRegistry();
		try {
			afterSites = new SimCache().getSimManagerForSession(session.getId())
					.getAllSites();
		} catch (Exception e) {
			Assert.fail(ExceptionUtil.exception_details(e));
		}
		Assert.assertEquals(beforeSites.size() + 1, afterSites.size());
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

}
