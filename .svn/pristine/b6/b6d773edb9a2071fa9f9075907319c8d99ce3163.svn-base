package gov.nist.toolkit.testengine.scripts;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BuildCollections {
	File testkit;

	String sections[] = { "testdata", "tests", "examples", "selftest" };
	Map<String, List<String>> collections = new HashMap<String, List<String>>();
	boolean error;
	
	void write() {
		File collectionsDir = new File(testkit + File.separator + "collections");
		
		collectionsDir.mkdir();  // create if doesn't exist
		
		for (Iterator<String> it = collections.keySet().iterator(); it.hasNext(); ) {
			String collectionName = it.next();
			List<String> contents = collections.get(collectionName);
			File collectionFile = new File(collectionsDir + File.separator + collectionName + ".tc");
			StringBuffer buf = new StringBuffer();
			for (String testnum : contents) {
				buf.append(testnum).append("\n");
			}
			try {
				Io.stringToFile(collectionFile, buf.toString());
			} catch (IOException e) {
				System.out.println("Cannot write to " + collectionFile);
				error = true;
			}
		}
		
	}

	void add(String collection, String testnum) {
		List<String> c = collections.get(collection);
		if (c == null) {
			c = new ArrayList<String>();
			collections.put(collection, c);
		}
		if ( ! c.contains(testnum))
			c.add(testnum);
	}
	
	void tokenize(String tokenStr, String testnum) {
		StringTokenizer st = new StringTokenizer(tokenStr);
		while (st.hasMoreElements()) {
			String tok = st.nextToken();
			add(tok, testnum);
		}
	}
	
	void tokenize(File testDir) {
		File collFile = new File(testDir + File.separator + "collections.txt");
		if ( !collFile.exists()) {
			error = true;
			System.out.println(collFile + " does not exist");
		}
		String testnum = testDir.getName();
		String fileContents;
		try {
			fileContents = Io.stringFromFile(collFile);
			tokenize(fileContents, testnum);
		} catch (IOException e) {
			System.out.println("Cannot read " + collFile);
			error = true;
		}
	}

	void scan() {
		 error = false;
		for (int i=0; i<sections.length; i++) {
			String section = sections[i];

			File sectionFile = new File(testkit + File.separator + section);
			File testDirs[] = sectionFile.listFiles();

			if (testDirs == null) {
				System.out.println("No tests defined in " + section);
				error = true;
			}

			for (int t=0; t<testDirs.length; t++) {
				File testDir = testDirs[t];
				if (!testDir.isDirectory())
					continue;
				if (testDir.getName().equals(".svn"))
					continue;
				File tagsFile = new File(testDir + File.separator + "collections.txt");
				if ( !tagsFile.exists()) {
					System.out.println("Test dir " + testDir + " has no collections.txt file");
					error = true;
					continue;
				}
				tokenize(testDir);
			}
		}
		//System.out.println(collections);
	}
	
	void delete() {
		File collectionsDir = new File(testkit + File.separator + "collections");
		String[] contents = collectionsDir.list();
		if (contents == null)
			return;
		for (int i=0; i<contents.length; i++) {
			String filename = contents[i];
			File contentsFile = new File(collectionsDir + File.separator + filename);
			contentsFile.delete();
		}
	}
	
	public static void main(String[] args) {
		BuildCollections bc = new BuildCollections();
		bc.testkit = new File(args[0]);
		System.out.println("Collections will be written to " + bc.testkit);
		bc.scan();
		bc.delete();
		bc.write();
	}

}
