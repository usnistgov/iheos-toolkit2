package gov.nist.toolkit.xdstools2.server.test.java.simulatorServiceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimManagerTest {
	static File warHome = new File("/home/bill/Documents/sf/toolkit/xdstools2/war");
	Session session = new Session(warHome, SiteServiceManager.getSiteServiceManager(), "sessionId1");
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
			sim = ssm.getNewSimulator(ATFactory.ActorType.REGISTRY.getName());
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
				session.deleteSim(sc.getId());
			}
			sim = null;
		}
	}

}
