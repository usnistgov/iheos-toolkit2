package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.*
import gov.nist.toolkit.installation.Installation

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SimpleRepositoryTest {
	// Create temp folder to be the External Cache
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder()

	@Before
	void initializeExternalCache() {
		Installation.installation().externalCache = tempFolder.newFolder()
	}
	
	@Test 
	void createRepositoryTest() {
		Repository rep = new RepositoryFactory().createRepository(SIMPLE, 'MyRepository', "This is my repository")
	}

}
