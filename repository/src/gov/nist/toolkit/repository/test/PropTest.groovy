package gov.nist.toolkit.repository.test;

import static org.junit.Assert.*
import gov.nist.toolkit.repository.Repository
import gov.nist.toolkit.repository.SiteRepositoryType

import org.junit.Test

class PropTest {

	@Test
	def void propSaveRestoreTest() {
		def repType = new SiteRepositoryType()
		def file = new File('/Users/bill/tmp/newout.txt')
		Properties orig = new Properties()
		orig.setProperty('a', 'b')
		orig.setProperty('x', 'y')
		new Repository(repType).saveProperties(orig, file)
		
		Properties properties = new Properties()
		properties = new Repository(repType).loadProperties(file)
				
		assertTrue orig.x.equals(properties.x)
		assertEquals orig.x, properties.x
		assertEquals orig, properties
	}
}
