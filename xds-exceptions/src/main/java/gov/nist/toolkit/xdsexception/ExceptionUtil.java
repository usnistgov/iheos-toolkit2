package gov.nist.toolkit.xdsexception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtil {

	static public String exception_details(Throwable e, String message) {
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
	
	static public String stack_trace(Exception e, int num_lines) {
		return firstNLines(stack_trace(e), num_lines, 2);
	}
	
	static public String stack_trace(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		return new String(baos.toByteArray());
	}

	static public String exception_details(Throwable e) {
		return exception_details(e, null);
	}

	static public String exception_details(Throwable e, int numLines) {
		return firstNLines(exception_details(e), numLines);
	}
	
	static public String exception_local_stack(Exception e) {
		StringBuffer buf = new StringBuffer();

		String[] lines = exception_details(e).split("\n");
		for (int i=0; i<lines.length; i++) {
			String line = lines[i];
			if (line.indexOf("gov.nist") != -1)
				buf.append(line).append("\n");
		}
		
		return buf.toString();
	}

	static public String here(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			return exception_details(e, message).replace("<", "[");
		}
	}

	static public String firstNLines(String string, int n) {
		int skipFirst = 0;
		return firstNLines(string, n, skipFirst);
	}

	public static String firstNLines(String string, int n, int skipFirst) {
		int startingAt = 0;
		int copyFrom = 0;
		n += skipFirst;   
		for (int line=0; line<n; line++) {
			if (line == skipFirst)
				copyFrom = startingAt;
			if (startingAt != -1)
				startingAt = string.indexOf('\n', startingAt + 1) + 1;
		}
		if (startingAt == -1) return string;
		return string.substring(copyFrom, startingAt);
	}

}
