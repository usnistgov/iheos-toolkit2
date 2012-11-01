/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.direct.utils;

import java.util.ArrayList;
import java.util.List;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;

public class TextErrorRecorderModif extends TextErrorRecorder implements ErrorRecorder {
	
	public int lastErrCount = 0;
	public List<ErrorInfo> errMsgs = new ArrayList<ErrorInfo>();
	
	public TextErrorRecorderModif() {
		super();
	}
	
	class ErrorInfo {
		int indent = 0;
		String msg = "";
		String resource = "";
		boolean isError = true;
	}
	
	@Override
	public void err(String code, String msg, String location, String resource, String logMessage) {
		ErrorInfo ei = new ErrorInfo();
		ei.indent = 2;
		ei.msg = msg;
		ei.resource = resource;
		errMsgs.add(ei);
		lastErrCount++;
	}
	
	// used for labeling major validation sections
	@Override
	public void sectionHeading(String msg) {
		ErrorInfo ei = new ErrorInfo();
		ei.isError = false;
		ei.indent = 0;
		ei.msg = msg;
		errMsgs.add(ei);
	}
	
	@Override
	public void detail(String msg) {
		ErrorInfo ei = new ErrorInfo();
		ei.isError = false;
		ei.indent = 3;
		ei.msg = msg;
		errMsgs.add(ei);
	}
	
	@Override
	public void warning(String code, String msg, String location, String resource) {
		ErrorInfo ei = new ErrorInfo();
		ei.isError = false;
		ei.indent = 1;
		ei.msg = msg;
		ei.resource = resource;
		errMsgs.add(ei);
	}

	public void challenge(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void err(Code arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}

	public void err(Code arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	public void err(Code arg0, String arg1, Object arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	public void err(Code arg0, String arg1, String arg2, String arg3,
			Object arg4) {
		// TODO Auto-generated method stub
		
	}

	public void err(Code arg0, String arg1, String arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		
	}

	public void externalChallenge(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void finish() {
		// TODO Auto-generated method stub
		
	}

	public boolean hasErrors() {
		boolean hasError = false;
		for(int i=0;i<errMsgs.size();i++) {
			if (errMsgs.get(i).isError) {
				hasError = true;
			}
		}
		return hasError;
	}

	public void showErrorInfo() {
		// TODO Auto-generated method stub
		
	}

	public void warning(Code arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (ErrorInfo ei : errMsgs) {
			if (ei.indent == 2) {
				buf.append("********");
			} else if (ei.indent == 0) {
				buf.append("\n##################\n");
			}		
			else if (ei.indent == 3) {
			//for (int i=0; i<ei.indent; i++)
				//buf.append("\t");
				//buf.append("");
			}
			else if (ei.indent == 1) {
				buf.append("!!!!!!Warning:  ");
			}
			if (ei.indent == 2)
				buf.append("Error:  ");
			buf.append(ei.msg);
			if (ei.resource != null && !ei.resource.equals(""))
				buf.append("   (" + ei.resource + ")");
			if (ei.indent == 0)
				buf.append("\n##################\n");
			buf.append("\n");
		}
		return buf.toString();
	}
	
}
