package gov.nist.toolkit.http

import gov.nist.toolkit.errorrecording.ErrorRecorder

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
		return line
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

	public String toHeaderString() {
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
