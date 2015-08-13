package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.utilities.io.LinesOfFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TestCollection {
	File testkit;
	File colDir;
	File collection;
	
	// intentionally labeled for internal use only 
	private TestCollection(File testkit) throws Exception {
		this.testkit = testkit;
		colDir = collectionsDir();
		collection = null;
	}
	
	public String toString() {
		return "TestCollection: collectionFile=" + collection;
	}
	
	public TestCollection(File testkit, String testCollectionName) throws Exception {
		this.testkit = testkit;
		colDir = collectionsDir();
		collection = new File(colDir.toString() + File.separatorChar + testCollectionName + ".tc");
		if ( !collection.exists())
			throw new Exception("Collection definition file " + collection.toString() + " does not exist");
	}
	
	File collectionsDir() throws Exception {
		File dir = new File(testkit + File.separator + "collections");
		if ( !dir.isDirectory())
			throw new Exception("Collections: path " + testkit + " is not a testkit or this testkit contains no collections");
		return dir;
	}
	
	static public List<String> getAllCollectionNames(File testkit) throws Exception {
		List<String> list = new ArrayList<String>();
		
		TestCollection tc = new TestCollection(testkit);
		
		File[] files = tc.colDir.listFiles();
		for (int i=0; i<files.length; i++) {
			if (files[i].getName().trim().endsWith(".tc"))
				list.add(files[i].getName());
		}
		
		return list;
	}
	
	public static void listTestKitCollections(File testkit) throws Exception {
		List<String> names = TestCollection.getAllCollectionNames(testkit);
		for (String name : names) {
			System.out.println(name);
		}
	}
	
	public List<TestDetails> getTestSpecs() throws Exception {
		List<TestDetails> specs = new ArrayList<TestDetails>();
		
		for (LinesOfFile lof=new LinesOfFile(collection); lof.hasNext(); ) {
			String line = lof.next();
			int commentStarts = line.indexOf('#');
			if (commentStarts != -1)
				line = line.substring(0, commentStarts);
			if (line.length() == 0)
				continue;
			List<String> tokens = tokenize(line);
			if (tokens.size() == 0)
				continue;
			String testNum = tokens.remove(0);
			
			TestDetails ts = new TestDetails(testkit, testNum);
			if (tokens.size() > 0) 
				ts.selectSections(tokens);
			specs.add(ts);
		}
		
		return specs;
	}
	
	List<String> tokenize(String str) {
		List<String> list = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(str);
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		
		return list;
	}


}
