package gov.nist.toolkit.session.server.services;


import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.File;

public class TestLogCache {
	File cache;

	public TestLogCache(File cache) {
		this.cache = cache;
	}

	public File getTestDir(TestInstance testInstance) {
		if (testInstance.getTestSession() == null) return null;
		return getTestDir(testInstance.getTestSession(), testInstance);
	}

	public File getTestDir(TestSession testSession, TestInstance testInstance) {
		// find test directory under external_cache/TestLogCache/<sessionName>/

		File sessionFile = getSessionDir(testSession);
		if (sessionFile != null) {
			File[] files = sessionFile.listFiles();
			if (files != null) {
				for (File testDir : files) {
					if (!testDir.isDirectory())
						continue;
					if (testDir.getName().equals(testInstance.getId())) {
						return testDir;
					}
				}
			}
		}
		return null;

	}

	public File getSessionDir(TestSession testSession) {
		return new File(cache + File.separator + testSession.getValue());
	}
}
