package gov.nist.toolkit.session.server.conformanceTest;

public class MesaTestTest {
//	String mesaTestSession = "bill";
//	SiteSpec siteSpec = new SiteSpec("pub", ActorType.REGISTRY, null);
//	Map<String, Object> params2 = new HashMap<String, Object>();
//	boolean stopOnFirstFailure = true;
//	String sessionId = "MySession";
//	Session session = new Session(new File("/Users/bmajur/workspace/toolkit/xdstools2/war"), SiteServiceManager.getSiteServiceManager(), sessionId);
//	EnvSetting es = new EnvSetting(sessionId, "NA2012");
//	List<String> SECTIONS = new ArrayList<String>();
//	Map<String, String> params = new HashMap<String, String>();
//
//	SiteSpec siteSpec() {
//		SiteSpec ss = new SiteSpec("red", ActorType.REGISTRY, null);
//		ss.isTls = false;
//		return ss;
//	}
//
//	@Before
//	public void before() {
//		System.setProperty("XDSCodesFile", "/Users/bmajur/tmp/toolkit2/environment/NA2014/codes.xml");
//	}
//
//	//	@Test
//	public void buildTestData() {
//		siteSpec.isTls = false;
//		String testId = "12346";
//		ParamBuilder pbuilder = new ParamBuilder();
//		pbuilder.withParam("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		params.put("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		new XdsTestServiceManager(session).run(
//				mesaTestSession,
//				siteSpec,
//				testId,
//				SECTIONS,
//				params,
//				params2,
//				stopOnFirstFailure);
//	}
//
//	//	@Test
//	public void verifyTestData() {
//		String testId = "11901";
//		siteSpec.isTls = false;
//		params.put("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		new XdsTestServiceManager(session).run(mesaTestSession, siteSpec, testId, SECTIONS, params, params2, stopOnFirstFailure);
//	}
//
//	//	@Test
//	public void twoStepTest() {
//		String testId = "11966";
//		ParamBuilder pbuilder = new ParamBuilder();
//		pbuilder.withParam("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		List<Result> results = new XdsTestServiceManager(session).run(
//				mesaTestSession,
//				siteSpec(),
//				testId,
//				SECTIONS,
//				pbuilder.getSParms(),
//				pbuilder.getOParms(),
//				stopOnFirstFailure);
//		boolean pass = true;
//		for (Result result : results) {
//			if (!result.passed())
//				pass = false;
//		}
//		if (!pass) System.out.println(testId + ": failed");
//	}
//
//	class TestResult {
//		String testId;
//		List<AssertionResult> msgs = new ArrayList<AssertionResult>();
//		boolean passed = true;
//
//		TestResult(String testnum) { testId = testnum; }
//
//		public String toString() { return testId + ": " + lines(msgs.getRetrievedDocumentsModel(0).assertion, 1); }
//	}
//
//
//	// non-tls register transaction tests
////	@Test
//	public void registerTests() {
//		ParamBuilder pbuilder = new ParamBuilder();
//		pbuilder.withParam("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		String[] testsx = {
//				"11990",
//				"11991",
//				"11992",
//		};
//		String[] rb_tests = {
//				"11990",
//				"11991",
//				"11992",
//				"11993",
//				"11994",
//				"11995",
//				"11996",
//				"11997",
//				"11998",
//				"11999",
//				"12000",
//				"12001",
//				"12002",
//				"12004",
//				"12022",
//				"12084",
//				"12323",
//				"12326",
//				"12327",
//				"12329",
//				"12370",
//				"12379",
//		"12051" };
//
//		/*
//		 * Errors:
//		 *
//11996: Invalid Patient ID test
//Error Did not find expected string in error messages: XDSUnknownPatientId
//11998: Reject Submission, Patient ID on Replacement Document does not match Original
//Validator: ExtrinsicObject urn:uuid:7c2c3752-a95b-4608-b728-aa5f4a7b15db has status Deprecated instead of 'Approved'
//12002: Reject Add Document to Folder - Patient ID does not match
//Error Did not find expected string in error messages: XDSPatientIdDoesNotMatch
//12379: Extra Metadata
//Error Expected errorCode of XDSExtraMetadataNotSaved
//		 */
//		int failures = 0;
//		int ran = 0;
//		List<TestResult> testResults = new ArrayList<TestResult>();
//		for (String testId : rb_tests) {
//			TestResult testResult = new TestResult(testId);
//			List<Result> results = new XdsTestServiceManager(session).run(
//					mesaTestSession,
//					siteSpec(),
//					testId,
//					SECTIONS,
//					pbuilder.getSParms(),
//					pbuilder.getOParms(),
//					stopOnFirstFailure);
//			boolean pass = true;
//			for (Result result : results) {
//				if (!result.passed()) {
//					pass = false;
//					testResult.msgs.addAll(result.getFailedAssertions());
//					testResults.add(testResult);
//				}
//			}
//			ran++;
//			if (!pass) { System.out.println(testId + ": failed"); failures++; }
//		}
//		System.out.println("\n========================\ntests: " + ran + " ran " + failures + " failed\n\n");
//		for (TestResult tr : testResults) {
//			System.out.println(tr);
//		}
//		System.out.println("\n========================\n");
//	}
//
//	void prTestResults(List<TestResult> testResults) {
//		System.out.println("\n========================\n");
//		for (TestResult tr : testResults) {
//			System.out.println(tr.testId + ": " + lines(tr.msgs.getRetrievedDocumentsModel(0).assertion, 1));
//		}
//		System.out.println("\n========================\n");
//	}
//
//	@Test
//	public void goodRegisterTests() {
//		ParamBuilder pbuilder = new ParamBuilder();
//		pbuilder.withParam("$patientid$", "25d5fe7674a443d^^^&1.3.6.1.4.1.21367.2009.1.2.300&ISO");
//		List<TestResult> testResults = runtest("tc:R-good", pbuilder);
//		prTestResults(testResults);
//	}
//
//	List<TestResult> runtest(String testId, ParamBuilder pbuilder) {
//		List<TestResult> testResults = new ArrayList<TestResult>();
//		TestResult testResult = new TestResult(testId);
//		List<Result> results = new XdsTestServiceManager(session).run(
//				mesaTestSession,
//				siteSpec(),
//				testId,
//				SECTIONS,
//				pbuilder.getSParms(),
//				pbuilder.getOParms(),
//				stopOnFirstFailure);
//		for (Result result : results) {
//			if (!result.passed()) {
//				testResult.passed = false;
//				testResult.msgs.addAll(result.getFailedAssertions());
//				testResults.add(testResult);
//			}
//		}
//		return testResults;
//	}
//
//	// This parses the wierd results from the test engine  so that only the error message shows
//	String lines(String content, int numberOfLines) {
//		if (content == null) return content;
//		String[] lines = content.split("\n");
//		StringBuffer buf = new StringBuffer();
//		for (int i=1; i<lines.length && i-1<numberOfLines; i++) {
//			if (i > 0) buf.append("\n");
//			buf.append(lines[i]);
//		}
//		return buf.toString();
//	}
}
