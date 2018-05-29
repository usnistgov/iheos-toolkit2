package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Tooling access to the embedded copy of the XDS testkit. 
 * @author bill
 *
 */
public class TestKit {
	private File	testKit;

	static Logger logger = Logger.getLogger(TestKit.class);

	public TestKit(File testKit  /*, String sessionId  */) {
		this.testKit = testKit;
	}

	static public void delete(String environmentName, TestSession testSession) {
		File root = getRoot(environmentName, testSession);
		Io.delete(root);
	}

		static public File getRoot(String environmentName, TestSession testSession) {
		String sep = File.separator;
		StringBuilder buf = new StringBuilder();
		buf.append(Installation.instance().environmentFile().toString());
		buf.append(sep).append(environmentName);
		buf.append(sep).append("testkits");
		buf.append(sep).append(testSession.getValue());
		return new File(buf.toString());
	}

	static public void generateStructure(String environmentName, TestSession testSession) {
		File root = getRoot(environmentName, testSession);
		for (Sections section : Sections.values()) {
			File sectionFile = new File(root, section.getSection());
			sectionFile.mkdirs();
		}
	}

	static public boolean exists(String environmentName, TestSession testSession) {
		return getRoot(environmentName, testSession).exists();
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

	enum PluginType  { ASSERTION }

	public File getPluginDir(PluginType pluginType) {
		return new File(testKit.toString() + File.separator + "plugins" + File.separator + pluginType.name());
	}
	
	private String[] testkitSections = { "tests", "testdata", "examples", "utilities", "testdata-repository", "testdata-registry", "testdata-xdr" };
	
	/**
	 * Get File representing directory containing test definition
	 * @param testname - name (or number) of test
	 * @return File representing directory
	 * @throws Exception if test does not exist
	 */
	public TestDefinition getTestDef(String testname) throws Exception {

		File testdir = null;

		for (String section : testkitSections) {
			testdir = new File(testKit.toString() + File.separator + section + File.separator + testname);
			if (testdir.exists())
				if (testdir.isDirectory())
					return new TestDefinition(testdir);
		}
				
		throw new Exception("test " + testdir + " does not exist");
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
			TestDefinition tt = getTestDef(name);
			String description = tt.getTestTitle();
			testNames.put(name, description);
		}
		
		return testNames;

	}

	/**
	 * Get test names from collection.  Set is used incase of duplicates.
	 * @param collectionSetName
	 * @param collectionName
	 * @return
	 * @throws Exception
     */
	public List<String> getCollectionMembers(String collectionSetName, String collectionName) throws Exception {
		Set<String> names = new HashSet<>();

		String[] parts;
		File file = getCollectionFileByName(collectionSetName, collectionName);
		try {
			parts = Io.stringFromFile(file).split("\n");
		} catch (Exception e) {
			return new ArrayList<String>();
		}

		for (int i=0; i<parts.length; i++) {
			String name = parts[i];
			if (name == null)
				continue;
			name = name.trim();
			if (name.length() == 0)
				continue;
			names.add(name);
		}
		List<String> list = new ArrayList<>();
		list.addAll(names);
		return list;

	}

	public List<TestCollectionDefinitionDAO> getTestCollections(String collectionSetName) throws Exception {
		List<TestCollectionDefinitionDAO> defs = new ArrayList<>();

		File collectionDir = new File(testKit, collectionSetName);
		if (!collectionDir.exists() || !collectionDir.isDirectory())
			throw new Exception("Test collection set name " + collectionSetName + " does not exist");
		for (File collectionFile : collectionDir.listFiles()) {
			if (collectionFile.isDirectory()) continue;
			if (!collectionFile.getName().endsWith(".txt")) continue;
			String collectionId = stripFileType(collectionFile.getName());
			String collectionTitle = Io.stringFromFile(collectionFile);
			defs.add(new TestCollectionDefinitionDAO(collectionId, collectionTitle));
		}

		return defs;
	}

	private String stripFileType(String name) {
		String[] parts = name.split("\\.");
		if (parts.length == 0) return name;
		return parts[0];
	}


	/**
	 * Given the name of a collection, return File reference. This format is used by collections and actorcollections
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
	}

	public List<String> getTestdataRepositoryTests() {
		return getTestdataSetListing("testdata-repository");
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

	public File getTestKitDir() {
		return testKit;
	}

	@Override
	public String toString() {
		return testKit.toString();
	}
}
