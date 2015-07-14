package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpHeader {
	private String line;
	private String name;
	private String value;
	private Map<String, String> params;
	private List<String> unnamedParams;
	boolean parsed;
	ErrorRecorder er = null;

	public HttpHeader(String line) throws  ParseException {
		if (line == null)
			line = "";
		this.line = line;
		parsed = false;
		parse();
	}

	public HttpHeader(String line, ErrorRecorder er) throws ParseException {
		if (line == null)
			line = "";
		this.line = line;
		this.er = er;
		parsed = false;
		if (er != null)
			er.detail("Parsing HttpHeader: " + line.trim());
		parse();
	}
	
	public String asString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Header: ").append("name=").append(name);
		buf.append("  value=").append(value).append('\n');
		for (String name : params.keySet()) {
			String value = params.get(name);
			buf.append("\t\t").append(name).append("=").append(value).append('\n');
		}
		return buf.toString();
	}

	public String toString() {
		return line;
	}

	public String getName()  {
		return name;
	}

	public String getValue()  {
		return value;
	}

	public Map<String, String> getParams()  {
		return params;
	}
	
	public List<String> getUnnamedParams() {
		return unnamedParams;
	}

	public String getParam(String name)  {
		if (params == null)
			return "";
		return params.get(name);
	}

	public boolean hasParam(String name) {
		String v = getParam(name);
		return (v != null && !v.equals(""));
	}
	
	public void parse() throws ParseException {
		if (parsed)
			return;
		parsed = true;

		HttpHeaderParser hp = new HttpHeaderParser(line);
		hp.parse();
		name = hp.getName();
		value = hp.getValue();
		unnamedParams = hp.getUnnamedParams();
		params = hp.getParams();
	}

	public void parseOld()  {
		if (parsed)
			return;
		parsed = true;
		int colonI = line.indexOf(':');
		if (colonI == -1) 
			return ; //throw new HttpHeaderParseException("Expected HTTP header with format: [headername : headervalue] found [" + line + "] instead");

		name = line.substring(0, colonI).trim();
		String rest = line.substring(colonI+1);
		String[] parts = rest.split(";");

		params = new HashMap<String, String>();
		unnamedParams = new ArrayList<String>();

		if (parts.length == 0 || parts.length == 1) {
			value = rest.trim();
			return;
		}

		for (int i=0; i<parts.length; i++ ) {
			if (i == 0) {
				value = parts[i].trim();
				continue;
			}
			String[] parts2 = parts[i].split("=");
			if (parts2.length != 2) {
				unnamedParams.add(parts[i]);
//				throw new HttpHeaderParseException("Does not parse as parameter: " + parts[i]);
			} else {
				String v = parts2[1].trim();
				if (v != null && v.startsWith("\"") && v.endsWith("\""))
					v = v.substring(0, v.length()-1).substring(1);
				params.put(parts2[0].trim(), v);
			}
		}
	}

	public String toHeader() {
		StringBuffer buf = new StringBuffer();

		buf.append(name);
		buf.append(": ");
		buf.append(value);

		for (Iterator<String> it=params.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			String val = params.get(key);
			buf.append("; ");
			buf.append(key);
			buf.append("=");
			buf.append(val);
		}

		return buf.toString();
	}


	public class HttpHeaderParseException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		HttpHeaderParseException(String msg) {
			super(msg);
		}
	}

}
