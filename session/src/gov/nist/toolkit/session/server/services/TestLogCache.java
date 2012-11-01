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

	
	
	/**
	 * Scans test log cache returning essential status of each test run.  Map
	 * key is test name, value is collection of error messages.  Each error message
	 * is prefixed by test section if one exists.
	 * @param sessionName
	 * @return test log cache status
	 * @throws Exception 
	 */
//	public Map<String, List<String>> scan(String sessionName) throws Exception {
//		Map<String, List<String>> results = new HashMap<String, List<String>>();
//		
//		File testSession = new File(cache.toString() + File.separator + sessionName);
//		if ( !( testSession.exists() && testSession.isDirectory()))
//			throw new Exception("Test session " + sessionName + " does not exist");
//		
//		File testdata = new File(testSession.toString() + File.separator + "testdata");
//		if (testdata.exists() && testdata.isDirectory()) {
//			// have testdata to process
//		}
//		
//		File tests = new File(testSession.toString() + File.separator + "tests");
//		if (tests.exists() && tests.isDirectory()) {
//			// have tests to process
//			
//			for (File test : tests.listFiles()) {
//				if (!test.isDirectory())
//					continue;
//				Map<String, List<String>> aresult = scanTest(test);
//				results.putAll(aresult);
//			}
//		}
//		
//		return null;
//	}
	
//	Map<String, List<String>> scanTest(File test) throws FactoryConfigurationError, Exception {
//		Map<String, List<String>> results = new HashMap<String, List<String>>();
//		
//		File log;
//		
//		log = new File(test.toString() + File.separator + "log.xml");
//		if (log.exists()) {
//			LogFileContent lf = new LogFileContent(log);
//			if (lf.isSuccess()) {
//				results.put(test.getName(), new ArrayList<String>());
//				return results;
//			}
//			Map<String, TestStepLogContent> stepMap = lf.getStepMap();
//			for (String stepName : stepMap.keySet()) {
//				TestStepLogContent stepLog = stepMap.get(stepName);
//				if (stepLog.getStatus())
//					continue;
//				List<String> errors = getErrors(stepLog);
//				results.put(test.getName() + File.separator + stepName, errors);
//			}
//		} else {
//			for (File d : test.listFiles()) {
//				if (!d.isDirectory())
//					continue;
//				log = new File(d.toString() + File.separator + "log.xml");
//				if (!log.exists())
//					continue;
//				LogFileContent lf = new LogFileContent(log);
//				if (lf.isSuccess()) {
//					results.put(test.getName() + File.separator + d.getName(), new ArrayList<String>());
//					continue;
//				}
//				Map<String, TestStepLogContent> stepMap = lf.getStepMap();
//				for (String stepName : stepMap.keySet()) {
//					TestStepLogContent stepLog = stepMap.get(stepName);
//					if (stepLog.getStatus())
//						continue;
//					List<String> errors = getErrors(stepLog);
//					results.put(d.getName() + File.separator + stepName, errors);
//				}
//			}
//			
//		}
//		
//		
//		return results;
//	}
	
//	List<String> getErrors(TestStepLogContent stepLog) throws Exception {
//		List<String> errors = new ArrayList<String>();
//		errors.addAll(stepLog.getSoapFaults());
//		errors.addAll(stepLog.getErrors());
//		errors.addAll(stepLog.getAssertionErrors());
//		return errors;
//	}
	
}
