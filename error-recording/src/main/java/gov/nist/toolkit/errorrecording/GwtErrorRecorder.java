package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingCompletionType;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingLevel;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class GwtErrorRecorder implements ErrorRecorder  {
	
	ErrorRecorderBuilder errorRecorderBuilder;
	List<ErrorRecorder> children = new ArrayList<>();
	List<ValidatorErrorItem> summary = new ArrayList<>();
	List<ValidatorErrorItem> errMsgs = new ArrayList<>();
	int lastErrCount = 0;
	
	static Logger logger = Logger.getLogger(GwtErrorRecorder.class);

	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (ValidatorErrorItem info : errMsgs) {
			buf.append(info).append("\n");
		}
		
		return buf.toString();
	}
	
	public String errToString() {
		StringBuffer buf = new StringBuffer();
		
		for (ValidatorErrorItem info : errMsgs) {
			if (info.level == ValidatorErrorItem.ReportingLevel.ERROR)
				buf.append(info.getCodeString() + ": " + info.msg).append("\n");
		}
		
		return buf.toString();
	}
	
	public List<String> getErrorMessages() {
		List<String> msgs = new ArrayList<String>();
		
		for (ValidatorErrorItem info : errMsgs) {
			if (info.level != ReportingLevel.ERROR)
				continue;
			msgs.add(info.msg);
		}
		
		return msgs;
	}
	
	public List<String> getErrorCodes() {
		List<String> codes = new ArrayList<String>();
		
		for (ValidatorErrorItem info : errMsgs) {
			if (info.level != ReportingLevel.ERROR)
				continue;
			codes.add(info.getCodeString());
		}
		
		return codes;
	}
		
	public List<ValidatorErrorItem> getValidatorErrorItems() {
		return errMsgs;
	}
	
	public List<ValidatorErrorItem> getSummaryErrorInfo() {
		return summary;
	}
	
	public boolean hasErrors() {
		for (ValidatorErrorItem vei : errMsgs) {
			if (vei.isError()) return true;
		}
		return false;
	}

	public boolean hasErrorsOrContext() {
		for (ValidatorErrorItem vei : errMsgs) {
			if (vei.isErrorOrContext()) return true;
		}
		return false;
	}

	public void err(Code code, String msg, String location, String resource) {
		if (msg == null || msg.trim().equals(""))
			return;
//		logger.debug(ExceptionUtil.here("err - " + msg));
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.ERROR;
		ei.msg = msg;
		ei.setCode(code);
		ei.location = location;
		ei.resource = resource;
		ei.completion = ValidatorErrorItem.ReportingCompletionType.ERROR;
		errMsgs.add(ei);
		lastErrCount++;
		propagateError();
	}

	// propogate error labeling to previous CHALLENGE
	// so context of error is sent/viewed
	private void propagateError() {
		logger.debug("propagating errors");
		for (int i=errMsgs.size()-2; i>=0; i--) {
			ValidatorErrorItem ei = errMsgs.get(i);
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
		for (ValidatorErrorItem msg : errMsgs) {
			if (msg.level == ValidatorErrorItem.ReportingLevel.ERROR)
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
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.SECTIONHEADING;
		ei.msg = msg;
		errMsgs.add(ei);
	}
	
	public void sectionHeadingError(String msg) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.SECTIONHEADING;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	public void challenge(String msg) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.CHALLENGE;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	public void showErrorInfo() {
	}

	public void detail(String msg) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.DETAIL;
		ei.msg = msg;
		errMsgs.add(ei);
	}

    @Override
    public void report(String name, String found) {
        detail(name + ": " + found);
    }

    public void externalChallenge(String msg) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.EXTERNALCHALLENGE;
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
			System.out.println("Got Error");
		boolean isWarning = (severity == null) ? false : ((severity.indexOf("Warning") != -1));
		ReportingCompletionType ctype = (isWarning) ? ValidatorErrorItem.ReportingCompletionType.WARNING : ValidatorErrorItem.ReportingCompletionType.ERROR;
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = (isWarning) ? ValidatorErrorItem.ReportingLevel.WARNING : ValidatorErrorItem.ReportingLevel.ERROR;
		ei.msg = msg;
		ei.setCode(code);
		ei.location = location;
		ei.resource = resource;
		ei.completion = ctype;
		errMsgs.add(ei);
		lastErrCount++;
		for (int i=errMsgs.size()-1; i>0; i--) {
			if (ei.level == ValidatorErrorItem.ReportingLevel.SECTIONHEADING)
				break;
			if (ei.level == ValidatorErrorItem.ReportingLevel.CHALLENGE) {
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
	public ErrorRecorder buildNewErrorRecorder() {
		ErrorRecorder er =  errorRecorderBuilder.buildNewErrorRecorder();
        children.add(er);
        return er;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

	@Override
	public int getNbErrors() {
		int nbErrors = 0;
		for (ValidatorErrorItem vei : errMsgs) {
			if ((vei.level == ValidatorErrorItem.ReportingLevel.ERROR) || (vei.level == ValidatorErrorItem.ReportingLevel.D_ERROR))
				nbErrors++;
		}
		return nbErrors;
	}

	@Override
	public void concat(ErrorRecorder er) {
		this.errMsgs.addAll(er.getErrMsgs());
	}
	
	public List<ValidatorErrorItem> getErrMsgs() {
		return this.errMsgs;
	}

	@Override
	public List<ErrorRecorder> getChildren() {
		return children;
	}

	@Override
	public void success(String dts, String name, String found, String expected, String RFC) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.D_SUCCESS;
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
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.D_ERROR;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Error";
		ei.completion = ValidatorErrorItem.ReportingCompletionType.ERROR;
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
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.D_WARNING;
		ei.dts = dts;
		ei.name = name;
		ei.found = found;
		ei.expected = expected;
		ei.rfc = RFC;
		ei.status = "Warning";
		ei.completion = ValidatorErrorItem.ReportingCompletionType.WARNING;
		errMsgs.add(ei);
		lastErrCount++;
		for (int i=errMsgs.size()-1; i>0; i--) {
			if (ei.level == ValidatorErrorItem.ReportingLevel.SECTIONHEADING)
				break;
			if (ei.level == ValidatorErrorItem.ReportingLevel.CHALLENGE) {
				ei.completion = ValidatorErrorItem.ReportingCompletionType.WARNING;
			}
		}
		
	}
	
	@Override
	public void info(String dts, String name, String found, String expected, String RFC) {
		tagLastInfo2();
		ValidatorErrorItem ei = new ValidatorErrorItem();
		ei.level = ValidatorErrorItem.ReportingLevel.D_INFO;
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
		ValidatorErrorItem ei = new ValidatorErrorItem();
		if(success) {
			ei.level = ValidatorErrorItem.ReportingLevel.D_SUCCESS;
			ei.status = "Success";
		} else {
			ei.level = ValidatorErrorItem.ReportingLevel.D_ERROR;
			ei.status = "Error";
		}
		ei.summaryPart = part;
		ei.msg = msg;
		summary.add(ei);
	}
	
	public void addValidatorItem(ValidatorErrorItem e) {
		errMsgs.add(e);
	}

	public int depth() {
		int depth = 1;

		int maxChildDepth = 0;
		for (ErrorRecorder er : children) {
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
