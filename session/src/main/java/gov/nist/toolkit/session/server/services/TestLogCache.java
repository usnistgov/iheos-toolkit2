package gov.nist.toolkit.session.server.services;


import gov.nist.toolkit.results.client.TestInstance;

import java.io.File;

public class TestLogCache {
	File cache;

	public TestLogCache(File cache) {
		this.cache = cache;
	}

	public File getTestDir(TestInstance testInstance) {
		if (testInstance.getUser() == null) return null;
		return getTestDir(testInstance.getUser(), testInstance);
	}

	public File getTestDir(String sessionName, TestInstance testInstance) {
		// find test directory under external_cache/TestLogCache/<sessionName>/

		for (File testDir : getSessionDir(sessionName).listFiles()) {
			if (!testDir.isDirectory())
				continue;
			if (testDir.getName().equals(testInstance.getId())) {
				return testDir;
			}

		}
		return null;

	}

	public File getSessionDir(String sessionName) {
		return new File(cache + File.separator + sessionName);
	}
}
