package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonServiceManager {
//	Session session = null;
	PropertyServiceManager propertyServiceMgr = null;
	SiteServiceManager siteServiceMgr = null;
	
	public CommonServiceManager() {
	
	}

//	SiteServiceManager siteServiceManager() {
//		if (siteServiceMgr == null)
//			siteServiceMgr = new SiteServiceManager();
//		return siteServiceMgr;
//	}

//	PropertyServiceManager propertyServiceManager() {
//		if (propertyServiceMgr == null)
//			propertyServiceMgr = new PropertyServiceManager();
//		return propertyServiceMgr;
//	}

//	protected String getSessionIdIfAvailable(Session session) {
//		return session.getId();
//	}
	
	protected List<Result> asList(Result r) {
		List<Result> lst = new ArrayList<Result>();
		lst.add(r);
		return lst;
	}

	protected List<Result> buildExtendedResultList(Exception e) {
		Result r = new Result();
		r.addAssertion(ExceptionUtil.exception_details(e), false);
		return this.asList(r);
	}

	protected Map<String, String> dup(Map<String, String> in) {
		Map<String, String> m = new HashMap<String, String>();

		for (String key : in.keySet()) {
			m.put(key, in.get(key));
		}

		return m;
	}

	protected String listAsString(List<String> lst) {
		StringBuffer buf = new StringBuffer();

		for (String i : lst) {
			buf.append(i).append("\n");
		}

		return buf.toString();
	}


	public List<Result> buildResultList(Exception e) {
		Result r = new Result();
		r.addAssertion(e.getMessage(), false);
		return this.asList(r);
	}

	public List<Result> buildResultList(String errorMsg) {
		Result r = new Result();
		r.addAssertion(errorMsg, false);
		return this.asList(r);
	}

	public Result buildResult(Exception e) {
		Result r = new Result();
		r.addAssertion(ExceptionUtil.exception_details(e), false);
		return r;
	}

	public Result buildResult() {
		return new Result();
	}



}
