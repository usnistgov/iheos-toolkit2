package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.client.GwtValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TextErrorRecorder implements ErrorRecorder {
	List<ErrorRecorder> children = new ArrayList<>();

	public class ErrorInfo {
		public int indent = 0;
		public String msg = "";
		public String resource = "";
		public boolean isError = true;
	}
	
	public List<ErrorInfo> errMsgs = new ArrayList<ErrorInfo>();
	
	public List<ErrorInfo> getErrorMsgs() {
		return errMsgs;
	}
	
	public void err(String msg, String resource) {
		ErrorInfo ei = new ErrorInfo();
		ei.indent = 2;
		ei.msg = msg;
		ei.resource = resource;
		ei.isError = true;
		errMsgs.add(ei);
		lastErrCount++;
	}

	public void err(Exception e) {
		err(exception_details(e, ""), "");
	}

	// used for labeling major validation sections
	public void sectionHeading(String msg) {
		tagLastInfo2();
		ErrorInfo ei = new ErrorInfo();
		ei.indent = 0;
		ei.msg = msg;
		ei.isError = false;
		errMsgs.add(ei);
	}

	// used to label individual validation sections
	public void challenge(String msg) {
		tagLastInfo2();
		ErrorInfo ei = new ErrorInfo();
		ei.indent = 1;
		ei.isError = false;
		ei.msg = msg;
		errMsgs.add(ei);
	}
	
	public void finish() {
		tagLastInfo2();
	}

	int lastErrCount = 0;
	public ErrorRecorderBuilder errorRecorderBuilder;

	
	int getLastErrCountChange() {
		int cnt = 0;
		for (ErrorInfo msg : errMsgs) {
			if (msg.indent == 2)
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
		
		ErrorInfo last = errMsgs.get(errMsgs.size() - 1);
//		if (last.indent == 1)
//			last.msg = last.msg + " - ok";
	}
	
	public void showErrorInfo() {
		System.out.println(toString());
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (ErrorInfo ei : errMsgs) {
			if (ei.indent == 2) {
				buf.append("********");
			} else {
			for (int i=0; i<ei.indent; i++)
				buf.append("\t");
			}
			if (ei.indent == 2)
				buf.append("Error: ");
			buf.append(ei.msg);
			if (ei.resource != null && !ei.resource.equals(""))
				buf.append("   (" + ei.resource + ")");
			buf.append("\n");
		}
		return buf.toString();
	}

	public void detail(String msg) {
		tagLastInfo2();
		ErrorInfo ei = new ErrorInfo();
		ei.isError = false;
		ei.indent = 1;
		ei.msg = msg;
		errMsgs.add(ei);
	}

	@Override
	public void report(String name, String found) {

	}

	public void externalChallenge(String msg) {
		tagLastInfo2();
		ErrorInfo ei = new ErrorInfo();
		ei.indent = 1;
		ei.msg = msg;
		ei.isError = false;
		errMsgs.add(ei);
	}

	public void err(String code, String msg, String location, String resource,
			Object logMessage) {
		err(msg, resource);
	}

	public void err(Code code, String msg, String location, String resource,
			Object log_message) {
		err(msg, resource);
	}

	public void err(Code code, String msg, String resource) {
		err(msg, resource);
	}

	public void err(Code code, Exception e) {
		err(e);
	}

	@Override
	public void err(Code code, Assertion assertion, String validatorModule, String location, String detail) {

	}

	@Override
	public void err(Code code, Assertion assertion, Object validatorModule, String location, String detail) {

	}

	public void err(Code code, String msg, String location, String resource) {
		err(msg, resource);
	}

	public void err(Code code, String msg, Object location, String resource) {
		err(msg, resource);
	}

	public boolean hasErrors() {
		for (ErrorInfo ei : errMsgs) {
			if (ei.isError)
				return true;
		}
		return false;
	}

	public void err(String code, String msg, String location, String severity,
			String resource) {
	}

	public void err(Code code, String msg, String location, String severity,
			String resource) {
		err(msg, resource);
	}

	@Override
	public void warning(String code, String msg, String location,
			String resource) {
		ErrorInfo ei = new ErrorInfo();
		ei.isError = false;
		ei.indent = 2;
		ei.msg = msg;
		ei.isError = false;
		ei.resource = resource;
		errMsgs.add(ei);
		lastErrCount++;
	}

	@Override
	public void warning(Code code, String msg, String location, String resource) {
		warning(code.toString(), msg, location, resource);
	}

	String exception_details(Exception e, String message) {
		if (e == null)
			return "No stack trace available";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);

		String emessage = e.getMessage();
		if (emessage == null || emessage.equals(""))
			emessage = "No Message";

		return ("Exception thrown: " + e.getClass().getName() + "\n" + 
		((message != null) ? message + "\n" : "") +
		emessage + "\n" + new String(baos.toByteArray()));
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder() {
		return this;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

	@Override
	public int getNbErrors() {
		return errMsgs.size();
	}

	public void concat(ErrorRecorder er) {

	}

	public List<GwtValidatorErrorItem> getErrMsgs() {
		return null;
	}

	@Override
	public void success(String location, String resource) {
		//detail(dts + ": " + name + " " + found);
	}

	@Override
	public void error(String dts, String name, String found, String expected, String RFC) {
		err(dts, name, "", "", dts);
	}

	@Override
	public void test(boolean good, String dts, String name, String found, String expected, String RFC) {

	}

	@Override
	public void warning(String dts, String name, String found, String expected, String RFC) {
		warning(dts, name, "", dts);
	}

	@Override
	public void info(String dts, String name, String found, String expected, String RFC) {
		detail(dts + ": " + name + " " + found);
	}

	@Override
	public void summary(String msg, boolean success, boolean part) {

	}

	@Override
	public List<ErrorRecorder> getChildren() {
		return children;
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
