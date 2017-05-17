package gov.nist.toolkit.errorrecording.gwt;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem.ReportingCompletionType;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem.ReportingLevel;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class GwtErrorRecorder implements IErrorRecorder {
	
	public IErrorRecorderBuilder errorRecorderBuilder;
	List<IErrorRecorder> children = new ArrayList<>();
	List<GwtValidatorErrorItem> summary = new ArrayList<>();
	List<GwtValidatorErrorItem> errMsgs = new ArrayList<>();
	int lastErrCount = 0;
	
	static Logger logger = Logger.getLogger(GwtErrorRecorder.class);

	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (GwtValidatorErrorItem info : errMsgs) {
			buf.append(info).append("\n");
		}
		
		return buf.toString();
	}
	
	public String errToString() {
		StringBuffer buf = new StringBuffer();
		
		for (GwtValidatorErrorItem info : errMsgs) {
			if (info.level == GwtValidatorErrorItem.ReportingLevel.ERROR)
				buf.append(info.getCodeString() + ": " + info.msg).append("\n");
		}
		
		return buf.toString();
	}
	
	public List<String> getErrorMessages() {
		List<String> msgs = new ArrayList<String>();
		
		for (GwtValidatorErrorItem info : errMsgs) {
			if (info.level != GwtValidatorErrorItem.ReportingLevel.ERROR)
				continue;
			msgs.add(info.msg);
		}
		
		return msgs;
	}
	
	public List<String> getErrorCodes() {
		List<String> codes = new ArrayList<String>();
		
		for (GwtValidatorErrorItem info : errMsgs) {
			if (info.level != GwtValidatorErrorItem.ReportingLevel.ERROR)
				continue;
			codes.add(info.getCodeString());
		}
		
		return codes;
	}
		
	public List<GwtValidatorErrorItem> getValidatorErrorItems() {
		return errMsgs;
	}
	
	public List<GwtValidatorErrorItem> getSummaryErrorInfo() {
		return summary;
	}
	
	public boolean hasErrors() {
		for (GwtValidatorErrorItem vei : errMsgs) {
			if (vei.isError()) return true;
		}
		return false;
	}

	public boolean hasErrorsOrContext() {
		for (GwtValidatorErrorItem vei : errMsgs) {
			if (vei.isErrorOrContext()) return true;
		}
		return false;
	}

	public void err(Code code, String msg, String location, String resource) {
		if (msg == null || msg.trim().equals(""))
			return;
//		logger.debug(ExceptionUtil.here("err - " + msg));
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.ERROR;
		ei.msg = msg;
		ei.setCode(code);
		ei.location = location;
		ei.resource = resource;
		ei.completion = GwtValidatorErrorItem.ReportingCompletionType.ERROR;
		errMsgs.add(ei);
		lastErrCount++;
		propagateError();
	}

	// propagate error labeling to previous CHALLENGE
	// so context of error is sent/viewed
	private void propagateError() {
		logger.debug("propagating errors");
		for (int i=errMsgs.size()-2; i>=0; i--) {
			GwtValidatorErrorItem ei = errMsgs.get(i);
			if (ei.level == ReportingLevel.SECTIONHEADING)
				break;  // too far
			if (ei.level == ReportingLevel.CHALLENGE) {
				ei.completion = ReportingCompletionType.ERROR;
				break; // done
			}
		}
		logger.debug("Results\n" + toString());
	}

	public void err(Code code, Exception e) {
		err(code, ExceptionUtil.exception_details(e), null, "");
	}

	public void finish() {
		tagLastInfo2();
	}

	int getLastErrCountChange() {
		int cnt = 0;
		for (GwtValidatorErrorItem msg : errMsgs) {
			if (msg.level == GwtValidatorErrorItem.ReportingLevel.ERROR)
				cnt++;
		}
		return cnt - lastErrCount;
	}
	
	void tagLastInfo2() {
		if (errMsgs.size() == 0)
			return;
		if (getLastErrCountChange() != 0) {
			lastErrCount = 0;
			return;
		}
		
	}
	
	public void sectionHeading(String msg) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.SECTIONHEADING;
		ei.msg = msg;
		errMsgs.add(ei);
	}
	
	public void sectionHeadingError(String msg) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.SECTIONHEADING;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	public void challenge(String msg) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.CHALLENGE;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	public void showErrorInfo() {
	}

	public void detail(String msg) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.DETAIL;
		ei.msg = msg;
		errMsgs.add(ei);
	}

    @Override
    public void report(String name, String found) {
        detail(name + ": " + found);
    }

    public void externalChallenge(String msg) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.EXTERNALCHALLENGE;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	// because of conflict in types, 5th parm is Object and down a few lines is
	// another method with 5th param of String, the compiler will generate a
	// call here.  This, the err1 stuff to disambiguate.
	public void err(Code code, String msg, String location, String resource,
			Object log_message) {
//		if (log_message != null && log_message instanceof String)
//			err1(code, msg, location, resource, log_message);
//		else
			err(code, msg, location, resource);
	}

	@Override
	public void err(XdsErrorCode.Code code, Assertion assertion, String validatorModule, String location, String detail) {

	}

	@Override
	public void err(XdsErrorCode.Code code, Assertion assertion, Object validatorModule, String location, String detail) {

	}

	@Override
	public void err(XdsErrorCode.Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage) {

	}

	@Override
	public void err(XdsErrorCode.Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage) {

	}

	@Override
	public void success(String location, String resource) {

	}

	@Override
	public void success(Assertion _assertion, Object _validatorModule, String _detail) {

	}

	public void err(String code, String msg, String location, String severity, String resource) {
		err1(code, msg, location, severity, resource);
	}

	public void err(Code code, String msg, Object location, String resource) {
		String loc = "";
		if (location != null)
			loc = location.getClass().getSimpleName();
		err(code, msg, loc, resource);
	}

//	public void err(String code, String msg, String location, String severity,
//			String resource) {
//		err1(code, msg, location, severity, resource);
//	}
	
	void err1(String code, String msg, String location, String severity,
			String resource) {
		if (msg == null || msg.trim().equals(""))
			return;
		logger.debug(ExceptionUtil.here("err - " + msg));
		if (severity.indexOf("Error") != -1)
			logger.debug("Got Error");
		boolean isWarning = (severity == null) ? false : ((severity.indexOf("Warning") != -1));
		ReportingCompletionType ctype = (isWarning) ? GwtValidatorErrorItem.ReportingCompletionType.WARNING : GwtValidatorErrorItem.ReportingCompletionType.ERROR;
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = (isWarning) ? GwtValidatorErrorItem.ReportingLevel.WARNING : GwtValidatorErrorItem.ReportingLevel.ERROR;
		ei.msg = msg;
		ei.setCode(code);
		ei.location = location;
		ei.resource = resource;
		ei.completion = ctype;
		errMsgs.add(ei);
		lastErrCount++;
		for (int i=errMsgs.size()-1; i>0; i--) {
			if (ei.level == GwtValidatorErrorItem.ReportingLevel.SECTIONHEADING)
				break;
			if (ei.level == GwtValidatorErrorItem.ReportingLevel.CHALLENGE) {
				ei.completion = ctype;
			}
		}
		
	}

	public void err(Code code, String msg, String location, String severity,
			String resource) {
		err1(code.toString(), msg, location, severity, resource);
	}

	@Override
	public void warning(String code, String msg, String location,
			String resource) {
		err1(code, msg, location, "Warning", resource);
	}

	@Override
	public void warning(Code code, String msg, String location, String resource) {
		err1(code.toString(), msg, location, "Warning", resource);
	}

	@Override
	public IErrorRecorder buildNewErrorRecorder() {
		IErrorRecorder er =  errorRecorderBuilder.buildNewErrorRecorder();
        children.add(er);
        return er;
	}

	@Override
	public IErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

	@Override
	public int getNbErrors() {
		int nbErrors = 0;
		for (GwtValidatorErrorItem vei : errMsgs) {
			if ((vei.level == GwtValidatorErrorItem.ReportingLevel.ERROR) || (vei.level == GwtValidatorErrorItem.ReportingLevel.D_ERROR))
				nbErrors++;
		}
		return nbErrors;
	}

	public void concat(GwtErrorRecorder er) {
		this.errMsgs.addAll(er.getErrMsgs());
	}
	
	public List<GwtValidatorErrorItem> getErrMsgs() {
		return this.errMsgs;
	}

	@Override
	public List<IErrorRecorder> getChildren() {
		return children;
	}

	@Override
	public void success(String dts, String name, String found, String expected, String RFC) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.D_SUCCESS;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Success";
		errMsgs.add(ei);
	}

	@Override
	public void error(String dts, String name, String found, String expected,String RFC) {
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.D_ERROR;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Error";
		ei.completion = GwtValidatorErrorItem.ReportingCompletionType.ERROR;
		errMsgs.add(ei);
		lastErrCount++;
		// propagate error labeling so context is given
		propagateError();

	}

	@Override
	public void test(boolean good, String dts, String name, String found, String expected, String RFC) {
		if (good) success(dts, name, found, expected, RFC);
		else error(dts, name, found, expected, RFC);
	}

	@Override
	public void warning(String dts, String name, String found, String expected, String RFC) {
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.D_WARNING;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Warning";
		ei.completion = GwtValidatorErrorItem.ReportingCompletionType.WARNING;
		errMsgs.add(ei);
		lastErrCount++;
		for (int i=errMsgs.size()-1; i>0; i--) {
			if (ei.level == GwtValidatorErrorItem.ReportingLevel.SECTIONHEADING)
				break;
			if (ei.level == GwtValidatorErrorItem.ReportingLevel.CHALLENGE) {
				ei.completion = GwtValidatorErrorItem.ReportingCompletionType.WARNING;
			}
		}
		
	}
	
	@Override
	public void info(String dts, String name, String found, String expected, String RFC) {
		tagLastInfo2();
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		ei.level = GwtValidatorErrorItem.ReportingLevel.D_INFO;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Info";
		errMsgs.add(ei);
	}

	@Override
	public void summary(String msg, boolean success, boolean part) {
		GwtValidatorErrorItem ei = new GwtValidatorErrorItem();
		if(success) {
			ei.level = GwtValidatorErrorItem.ReportingLevel.D_SUCCESS;
			ei.status = "Success";
		} else {
			ei.level = GwtValidatorErrorItem.ReportingLevel.D_ERROR;
			ei.status = "Error";
		}
		ei.summaryPart = part;
		ei.msg = msg;
		summary.add(ei);
	}
	
	public void addValidatorItem(GwtValidatorErrorItem e) {
		errMsgs.add(e);
	}

	public int depth() {
		int depth = 1;

		int maxChildDepth = 0;
		for (IErrorRecorder er : children) {
			int childDepth = er.depth();
			if (childDepth > maxChildDepth) maxChildDepth = childDepth;
		}

		return depth + maxChildDepth;
	}

	@Override
	public void registerValidator(Object validator) {

	}

	@Override
	public void unRegisterValidator(Object validator) {

	}

}
