package gov.nist.toolkit.xdsexception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;


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

		String stackTrace = new String(baos.toByteArray());
		StringBuilder buf = new StringBuilder();
		buf.append("\nException ").append(e.getClass().getSimpleName());
		Scanner scanner = new Scanner(stackTrace);
//		if (scanner.hasNextLine()) scanner.nextLine();  // heading
//		if (scanner.hasNextLine()) scanner.nextLine();  // here()
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (!line.contains("gov.nist.toolkit")) continue;
			buf.append("\n\t").append(line);
		}
//		stackTrace = buf.toString();

		StringBuilder msg = new StringBuilder();
		if (message != null)
			msg.append(message);
		if (emessage != null && !emessage.equals(message)) {
			if (msg.length() != 0) msg.append("\n");
			msg.append(emessage);
		}
		msg.append(buf);

//		=  ("Exception thrown: " + e.getClass().getName() + "\n" +
//		((message != null) ? message + "\n" : "") +
//		emessage + "\n" + stackTrace);
		return msg.toString();
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
			String str = exception_details(e, message).replace("<", "[");
			return str;
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
