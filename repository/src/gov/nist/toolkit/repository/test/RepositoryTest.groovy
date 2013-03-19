package gov.nist.toolkit.repository.test;

import static org.junit.Assert.*
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.repository.Repository
import gov.nist.toolkit.repository.SiteRepositoryType

import org.junit.Test

class RepositoryTest {

	@Test
	void fooTest() {
		Installation.installation().externalCache = new File('/Users/bill/tmp/test/toolkit')
		Repository rep = new Repository(new SiteRepositoryType())
		assertFalse rep.exists()
		assertFalse rep.isInitialized()
		rep.initialize()
		assertTrue rep.exists()
		assertTrue rep.isInitialized()
	}
}
