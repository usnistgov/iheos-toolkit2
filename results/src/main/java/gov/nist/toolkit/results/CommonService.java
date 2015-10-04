package gov.nist.toolkit.results;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestId;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonService {

	static public List<Result> asList(Result r) {
		List<Result> lst = new ArrayList<>();
		lst.add(r);
		return lst;
	}

	static public List<Result> asList(Result r1, Result r2) {
		List<Result> lst = new ArrayList<>();
		lst.add(r1);
		lst.add(r2);
		return lst;
	}

	static public List<Result> buildExtendedResultList(Throwable e) {
		Result r = ResultBuilder.RESULT(new TestId("test"));
		r.addAssertion(ExceptionUtil.exception_details(e), false);
		return asList(r);
	}

	static public Map<String, String> dup(Map<String, String> in) {
		Map<String, String> m = new HashMap<>();

		for (String key : in.keySet()) {
			m.put(key, in.get(key));
		}

		return m;
	}

//	static public  String listAsString(List<String> lst) {
//		StringBuffer buf = new StringBuffer();
//
//		for (String i : lst) {
//			buf.append(i).append("\n");
//		}
//
//		return buf.toString();
//	}


	static public List<Result> buildResultList(Exception e) {
		Result r = ResultBuilder.RESULT(new TestId("test"));
		if (e.getMessage() == null)
			r.addAssertion(ExceptionUtil.exception_details(e), false);
		else
			r.addAssertion(e.getMessage(), false);
		return asList(r);
	}

	static public List<Result> buildResultList(String errorMsg) {
		Result r = ResultBuilder.RESULT(new TestId("test"));
		r.addAssertion(errorMsg, false);
		return asList(r);
	}

	static public Result buildResult(Exception e) {
		Result r = ResultBuilder.RESULT(new TestId("test"));
		r.addAssertion(ExceptionUtil.exception_details(e), false);
		return r;
	}

	static public Result buildResult() {
		return ResultBuilder.RESULT(new TestId("test"));
	}



}
