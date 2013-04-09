package gov.nist.toolkit.repository.test;

import static org.junit.Assert.*
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.repository.Repository
import gov.nist.toolkit.repository.RepositoryItem
import gov.nist.toolkit.repository.SiteRepositoryType

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RepositoryTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder()
	
	@Test 
	void mapToFromPropertiesTest() {
		Repository rep = new Repository(new SiteRepositoryType())
		def orig = [a:'b', x:'y']
		def props = rep.mapToProperties(orig)
		def copy = rep.propertiesToMap(props)
		assertEquals orig, copy
	}

	@Test
	void simpleNameTest() {
		Repository rep = new Repository(new SiteRepositoryType())
		def simpleName

		simpleName = rep.getSimpleName(new File("/usr/project/foo.meta.txt"))
		assertEquals 'foo', simpleName

		simpleName = rep.getSimpleName(new File("/usr/project/foo"))
		assertEquals 'foo', simpleName
	}

	@Test
	void repositoryItemIOTest() {
		Installation.installation().externalCache = tempFolder.newFolder()
		Repository rep = new Repository(new SiteRepositoryType())
		
		def itemName = 'myname'
		RepositoryItem item = new RepositoryItem(itemName, testSiteProperties(), testContent())
		rep.addItem(item)
		RepositoryItem item2 = rep.getItem(itemName)
		assertTrue item.equals(item2)
	}
	
	@Test
	void repositoryItemBadNameTest() {
		Installation.installation().externalCache = tempFolder.newFolder()
		Repository rep = new Repository(new SiteRepositoryType())

		try {		
			RepositoryItem item = new RepositoryItem('my.name', testSiteProperties(), testContent())
		} catch (Error e) {
			return
		}
		fail 'did not throw assertion'
	}
	
	def testSiteProperties() {
		def props = new Properties()
		props.setProperty('format1', 'xml')
		props.setProperty('format2', '')
		props.setProperty('owner', 'bill')
		props.setProperty('scope', 'Private')
		return props
	}
		
	def testContent() {
		return '<foo><bar a="bbb"/></foo>'.bytes
	}
}

