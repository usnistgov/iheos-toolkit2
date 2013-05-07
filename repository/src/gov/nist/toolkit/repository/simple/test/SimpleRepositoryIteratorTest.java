package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleRepositoryIterator;
import gov.nist.toolkit.repository.simple.SimpleType;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleRepositoryIteratorTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");

	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
		new RepositoryFactory().createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple", ""));
	}

	@Test
	public void repositoryIteratorTest() throws RepositoryException {
		SimpleRepositoryIterator it = new SimpleRepositoryIterator();
		
		assertTrue (it.size() > 0);
		assertTrue (it.size() == it.remaining());
		assertTrue(it.hasNextRepository());
		it.nextRepository();
		assertTrue(it.size() == it.remaining() + 1);
	}
		

}
