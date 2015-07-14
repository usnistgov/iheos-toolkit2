package gov.nist.toolkit.results;

import java.util.Calendar;

import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

public class ResultBuilder {

	static public Result RESULT(String testName) {
		Calendar calendar = Calendar.getInstance();
		String timestamp = calendar.getTime().toString();
		Result r = new Result(timestamp);
		r.testName = testName;
		return r;
	}
	
	static public Result RESULT(String testName, AssertionResults assertions, AssertionResult assertion, Throwable t) {
		Calendar calendar = Calendar.getInstance();
		String timestamp = calendar.getTime().toString();
		Result r = new Result(timestamp);
		r.testName = testName;
		if (assertions != null) {
			if (r.assertions == null || r.assertions.size() == 0) {
				r.assertions = assertions;
			} else {
				r.assertions.add(assertions);
			}
		}
		if (assertion != null) {
			if (r.assertions == null)
				r.assertions = new AssertionResults();
			r.assertions.add(assertion);
		}
		if (t != null)
			r.addAssertion(ExceptionUtil.exception_details(t), false);
		r.pass = r.passed();
		return r;
	}
	

}
