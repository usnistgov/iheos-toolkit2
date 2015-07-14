package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Tooling access to the embedded copy of the XDS testkit. 
 * @author bill
 *
 */



public class TestKit {
	File	testKit;
//	String sessionId;

	static Logger logger = Logger.getLogger(TestKit.class);

	public TestKit(File testKit  /*, String sessionId  */) {
		this.testKit = testKit;
//		this.sessionId = sessionId;
	}
	
	/**
	 * Return File reference to "tests" part of Testkit
	 * @return
	 */
	public File getTestsDir() {
		return new File(testKit.toString() + File.separator + "tests");
	}

	/**
	 * Return File reference to "testdata" part of Testkit
	 * @return
	 */
	File getTestdataDir() {
		return new File(testKit.toString() + File.separator + "testdata");
	}

	
	String[] testkitSections = { "tests", "testdata", "xcpd", "examples" };
	
	/**
	 * Get File representing directory containing test definition
	 * @param testname - name (or number) of test
	 * @return File representing directory
	 * @throws Exception if test does not exist
	 */
	public File getTestDir(String testname) throws Exception {
		
		for (String section : testkitSections) {
			File testdir = new File(testKit.toString() + File.separator + section + File.separator + testname);
			if (testdir.exists() && testdir.isDirectory())
				return testdir;
		}
				
		throw new Exception("test " + testname + " does not exist");
	}
	
	/**
	 * Get test names and descriptions from a named test collection
	 * @param collectionSetName name of directory holding tc files (collection definitions)
	 * @param collectionName name of a collection 
	 * @return list of test name => description
	 * @throws Exception oops - collection doesn't exist or cannot be read
	 */
	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception {
		Map<String, String> testNames = new HashMap<String, String>();
		
		String[] parts = Io.stringFromFile(getCollectionFileByName(collectionSetName, collectionName)).split("\n");
		
		for (int i=0; i<parts.length; i++) {
			String name = parts[i];
			if (name == null)
				continue;
			name = name.trim();
			if (name.length() == 0)
				continue;
			TestDefinition tt = new TestDefinition(getTestDir(name));
			String description = tt.getTestDescription();
			testNames.put(name, description);
		}
		
		return testNames;

	}
	
	/**
	 * Given the name of a collection, return File reference.
	 * @param collectionSetName
	 * @param collectionName
	 * @return
	 */
	public File getCollectionFileByName(String collectionSetName, String collectionName) {
		File collectionFile = new File(
				testKit.toString() + File.separator +
				collectionSetName + File.separator +
				collectionName + ".tc"
				);
		
		return collectionFile;
	}
	
	/**
	 * Get map of (collection name, collection description) pairs contained in testkit
	 * @return map of name, description pairs
	 * @throws IOException
	 */
	public Map<String, String> getCollectionNames(String collectionSetName) throws IOException {
		File collectionsDir = new File(
				testKit.toString() + File.separator +
				collectionSetName 
				);
		
		if (!collectionsDir.exists() || !collectionsDir.isDirectory())
			throw new IOException("Collections directory (" + collectionsDir.toString() + ") cannot be read or is not a directory");
		
		
		String[] nameList = collectionsDir.list();
		Map<String, String> names = new HashMap<String, String>();
		for (String name : nameList) {
			if (!name.endsWith(".txt"))
				continue;
			File f = new File(collectionsDir.toString() + File.separator + name);
			String shortName = name.substring(0, name.indexOf(".txt"));
			names.put(shortName, Io.stringFromFile(f));
		}
		
		return names;
		
	}
	
/* These two "collections" are maintained separate from the other collections
 * so these two calls are necessary.  Should be migrated back into testkit.	
 */
	public List<String> getTestdataRegistryTests() {
		return getTestdataSetListing("testdata-registry");
//		List<String> tests = new ArrayList<String>();
//
//		File testdataDir = new File(testKit.toString() + File.separator + "testdata-registry");
//		String[] dirs = testdataDir.list();
//		for (int i = 0; i < dirs.length; i++) {
//			if (dirs[i].startsWith("."))
//				continue;
//			tests.add(dirs[i]);
//		}
//
//		return tests;
	}

	public List<String> getTestdataRepositoryTests() {
		return getTestdataSetListing("testdata-repository");
//		List<String> tests = new ArrayList<String>();
//
//		File testdataDir = new File(testKit.toString() + File.separator + "testdata-repository");
//		String[] dirs = testdataDir.list();
//		for (int i = 0; i < dirs.length; i++) {
//			if (dirs[i].startsWith("."))
//				continue;
//			tests.add(dirs[i]);
//		}
//
//		return tests;
	}
	
	public List<String> getTestdataSetListing(String testdataSetName) {
		List<String> tests = new ArrayList<String>();

		File testdataDir = new File(testKit.toString() + File.separator + testdataSetName);
		if (!testdataDir.exists() || !testdataDir.isDirectory())
			return tests;
		String[] dirs = testdataDir.list();
		for (int i = 0; i < dirs.length; i++) {
			if (dirs[i].startsWith("."))
				continue;
			String filename = dirs[i];
			int dot_i = filename.indexOf('.');
			if (dot_i != -1) {
				filename = filename.substring(0, dot_i);
			}
			tests.add(filename);
		}

		return tests;
	}

}
