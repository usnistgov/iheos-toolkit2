package gov.nist.toolkit.session.server.services;

import java.io.File;

public class TestLogCache {
	File cache;

	public TestLogCache(File cache) {
		this.cache = cache;
	}
	
	public File getTestDir(String sessionName, String testName) {
		// find test directory under external_cache/TestLogCache/<sessionName>/

		for (File area : getSessionDir(sessionName).listFiles()) {  // area is tests, testdata etc
			if (!area.isDirectory())
				continue;
			for (File testDir : area.listFiles()) {
				if (!testDir.isDirectory())
					continue;
				if (testDir.getName().equals(testName)) {
					return testDir;
				}
			}
		}
		return null;

	}
	
	public File getSessionDir(String sessionName) {
		return new File(cache + File.separator + sessionName);
	}	
}
