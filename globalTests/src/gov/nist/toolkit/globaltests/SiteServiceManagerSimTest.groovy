package gov.nist.toolkit.globaltests;

import static org.junit.Assert.*
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actortransaction.client.ATFactory
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager

import org.junit.Test

class SiteServiceManagerSimTest  {
	
	@Test
	public void getAllSites() {
		Installation.installation().externalCache(new File('/Users/bill/tmp/toolkit'))
		Session session = new Session(
			new File('/Users/bill/Documents/sfhg/workspace/toolkit/xdstools2/war'),
				SiteServiceManager.getSiteServiceManager() 
				)
		session.setEnvironment("NA2013")
		SimulatorServiceManager simSvcMgr = new SimulatorServiceManager(session)
		Simulator sim = simSvcMgr.getNewSimulator(ATFactory.ActorType.DOCUMENT_RECIPIENT.name)
		
		List<Site> sites = SiteServiceManager.getSiteServiceManager().getAllSites(session.getId());

		println sites.collect { site -> site.getName() }
		
		assertTrue sites.collect { site -> site.getName() }.contains('Private.rec')
	}

	@Test
	public void xtestSetup2() {
		Installation.installation().externalCache(new File('/Users/bill/tmp/toolkit'))
		Session session = new Session(
			new File('/Users/bill/Documents/sfhg/workspace/toolkit/xdstools2/war'),
				SiteServiceManager.getSiteServiceManager() 
				)
		session.setEnvironment("NA2013")
		SimulatorServiceManager simSvcMgr = new SimulatorServiceManager(session)
		Simulator sim = simSvcMgr.getNewSimulator(ATFactory.ActorType.DOCUMENT_RECIPIENT.name)
		
		List<String> siteNames = SiteServiceManager.getSiteServiceManager().getSiteNames(session.id(), false, true)
		
		assertTrue siteNames.contains('Private.rec')
	}

}
