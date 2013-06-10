package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleTypeIterator;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTypeIteratorTest {
	static File RootOfAllRepositories = new File("/e/artrep_test_resources/repositories");
	
	@BeforeClass
	static public void initializeConfiguration() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
	}
	
	@Test
	public void simpleTypeIteratorTest() throws RepositoryException {
		TypeIterator it = new SimpleTypeIterator();
		assertTrue("initially the iterator should have hasNextType() return true", it.hasNextType());
		
		Type nextType = it.nextType();
		
		assertNotNull("nextType() should return an object, not null", nextType);
		
		Type nextType2 = it.nextType();
		
		assertFalse("Two returned types should not be equal", nextType.isEqual(nextType2));
	}
	
	@Test
	public void managerIteratorTest() throws RepositoryException {
		TypeIterator ti = new RepositoryFactory().getRepositoryTypes();
		assertTrue("initially the iterator should have hasNextType() return true", ti.hasNextType());
		
		Type nextType = ti.nextType();
		
		assertNotNull("nextType() should return an object, not null", nextType);
		
		Type nextType2 = ti.nextType();
		
		assertFalse("Two returned types should not be equal", nextType.isEqual(nextType2));
	}
	
	
}
