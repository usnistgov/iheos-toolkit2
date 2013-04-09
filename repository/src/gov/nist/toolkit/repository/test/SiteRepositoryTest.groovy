package gov.nist.toolkit.repository.test

import static org.junit.Assert.*
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.repository.SiteRepository
import gov.nist.toolkit.repository.SiteRepositoryItem

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SiteRepositoryTest {
	// Create temp folder to be the External Cache
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder()

	// There is only one site repository.  Specifying the EC also
	// specifies the site repository
	@Before
	void initializeExternalCache() {
		Installation.installation().externalCache = tempFolder.newFolder()
	}
	
	@Test
	void saveRetrieveSiteTest() {
		// This gives a connection to site repository
		// It depends on the External Cache being initialized (above)
		SiteRepository siteRep = new SiteRepository()
		
		// Create an item in site repository
		def itemName = 'myname'
		SiteRepositoryItem item = siteRep.addItem(itemName, SiteRepository.Scope.PUBLIC, 'bill', '<mysite/>'.getBytes())

		// Verify contents of site repository
		SiteRepositoryItem item2 = siteRep.getItem(itemName)
		assertTrue item.equals(item2)
	}
	
	@Test
	void getAllPublicSitesTest() {
		SiteRepository siteRep = new SiteRepository()
		buildTestData(siteRep)
		
		// get all public sites
		def pubSites = siteRep.getPublicSiteNames()
		assertEquals 2, pubSites.size
		
		// getSitesFor gets public sites and private sites 
		// owned by specified owner
		def billSites = siteRep.getSiteNamesFor('bill')
		assertEquals 3, billSites.size 
	}

	def buildTestData(SiteRepository siteRep) {
		siteRep.addItem('bill1', SiteRepository.Scope.PUBLIC, 'bill', '<mysite/>'.getBytes())
		siteRep.addItem('lynn1', SiteRepository.Scope.PUBLIC, 'lynn', '<mysite/>'.getBytes())
		siteRep.addItem('bill2', SiteRepository.Scope.PRIVATE, 'bill', '<mysite/>'.getBytes())
		siteRep.addItem('lynn2', SiteRepository.Scope.PRIVATE, 'lynn', '<mysite/>'.getBytes())
	}
}
