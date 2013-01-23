package gov.nist.toolkit.osid.repository.file.test.java;

import gov.nist.toolkit.osid.repository.file.RepositoryManager;
import gov.nist.toolkit.osid.repository.file.RepositoryMapper;

import org.junit.Test;

public class RepositoryManagerTest {

	@Test
	public void repositoryCreation() {
		RepositoryManager rman = RepositoryMapper.getInstance().getDefaultRepositoryManager();
		rman.createRepository("My First", "", RepositoryMapper.getInstance().getDefaultRepositoryType());
	}
}
