package gov.nist.toolkit.repository.simple.test;

import static org.junit.Assert.*;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.RepositoryIterator;
import gov.nist.toolkit.repository.api.SharedException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleType;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleRepositoryTest {
	static File RootOfAllRepositories = new File("/Users/bmajur/tmp/repositories");
	static Id repId = null;
	
	// Create temp folder to be the External Cache
//	@Rule
//	public TemporaryFolder tempFolder = new TemporaryFolder()
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		new Configuration(RootOfAllRepositories);
		Repository rep = new RepositoryFactory().createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple", ""));
		repId = rep.getId();
	}
	
	@Test
	public void loadRepositoryTest() throws RepositoryException {
		RepositoryFactory repFact = new RepositoryFactory();
		repFact.getRepository(repId);
	}
		
	@Test 
	public void repositoryIteratorTest() throws SharedException {
		RepositoryFactory fact = new RepositoryFactory();
		Type simpleType = new SimpleType("simple", "");
		Repository rep1 = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple", ""));
		Id repId1 = rep1.getId();
		Type repType1 = rep1.getType();
		
		assertTrue("query for type simple should return a repository of type simple - got [" +
		    repType1.getDomain() + "] instead.", simpleType.isEqual(repType1));
		
		Repository rep2 = fact.createRepository(
				"This is my repository",
				"Description",
				new SimpleType("simple", ""));
		Id repId2 = rep2.getId();
		Type repType2 = rep2.getType();
		
		assertTrue("query for type simple should return a repository of type simple - got [" +
		    repType2.getDomain() + "] instead.", simpleType.isEqual(repType2));
		
		
		boolean found = false;
		for (RepositoryIterator ri=fact.getRepositoriesByType(simpleType); ri.hasNextRepository();) {
			Repository r = ri.nextRepository();
			if (repId1.isEqual(r.getId())) {
				found = true;
				break;
			}
		}
		assertTrue("repId1 not found", found);
		
		found = false;
		for (RepositoryIterator ri=fact.getRepositoriesByType(simpleType); ri.hasNextRepository();) {
			Repository r = ri.nextRepository();
			if (repId2.isEqual(r.getId())) {
				found = true;
				break;
			}
		}
		assertTrue("repId2 not found", found);
	}
}
