package gov.nist.toolkit.session.server.services;


import gov.nist.toolkit.results.client.TestId;

import java.io.File;

public class TestLogCache {
	File cache;

	public TestLogCache(File cache) {
		this.cache = cache;
	}
	
	public File getTestDir(String sessionName, TestId testId) {
		// find test directory under external_cache/TestLogCache/<sessionName>/

		for (File area : getSessionDir(sessionName).listFiles()) {  // area is tests, testdata etc
			if (!area.isDirectory())
				continue;
			for (File testDir : area.listFiles()) {
				if (!testDir.isDirectory())
					continue;
				if (testDir.getName().equals(testId.getId())) {
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
