package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class HttpParserBa {

	byte[] input;
	int from;
	int to = 0;
	boolean parsed = false;
	ErrorRecorder er = null;
	String charset = null;
	HttpMessageBa message = new HttpMessageBa();
	MultipartParserBa multiparser;
	boolean appendixV = true;

	public MultipartParserBa getMultipartParser() {
		return multiparser;
	}

	public HttpMessageBa getHttpMessage() {
		return message;
	}

	public void setBody(byte[] body) {
		message.body = body;
	}
	
	public byte[] getBody() {
		return message.body;
	}

	public String getRawContentId()  {
		String hdrStr = message.getHeader("content-id");
		if (hdrStr == null || hdrStr.equals(""))
			return null;
		try {
			HttpHeader hh = new HttpHeader(hdrStr);
			return hh.getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public String getCharset() {
		return charset;
	}

	public boolean isMultipart() {
		return message.multipart != null;
	}

	public MultipartMessageBa getMultipart() {
		return message.multipart;
	} 

	public void setErrorRecorder(ErrorRecorder er) {
		this.er = er;
	}

	public HttpParserBa() {

	}

	public HttpParserBa(HttpServletRequest request) throws IOException, HttpParseException {
		init(request);
	}

	public HttpParserBa(HttpServletRequest request, boolean appendixV) throws IOException, HttpParseException {
		this.appendixV = appendixV;
		init(request);
	}

	public HttpParserBa(HttpServletRequest request, ErrorRecorder er) throws IOException, HttpParseException {
		this.er = er;
		init(request);
	}

	public void init(HttpServletRequest request) throws IOException, HttpParseException {
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String name = e.nextElement();
			String value = request.getHeader(name);
			message.addHeader(name, value);
		}

		message.body = Io.getBytesFromInputStream(request.getInputStream());

		tryMultipart();
	}

	public HttpParserBa(byte[] msg) throws HttpParseException, ParseException  {
		er = null;
		init(msg, null);
	}

	public HttpParserBa(byte[] msg, ErrorRecorder er) throws HttpParseException, ParseException   {
		this.er = er;
		init(msg, null);
	}

	public void init(byte[] msg, HttpMessageBa hmessage) throws HttpParseException, ParseException  {
		input = msg;
		if (hmessage != null)
			message = hmessage;
		parse();
		tryMultipart();
	}

	public void tryMultipart() throws HttpParseException {
		try {
			multiparser = new MultipartParserBa(this, er, appendixV);
			message.multipart = multiparser.message;
		} catch (ParseException e) {
			// not a multipart
			message.multipart = null;
		} catch (HttpHeaderParseException e) {
			// not a multipart
			message.multipart = null;
		}
	}

	public void parse() throws HttpParseException, ParseException  {
		if (parsed)
			return;
		parsed = true;
		parseHeadersAndBody();
	}

	void validateTo() throws EoIException {
		if (to >= input.length)
			throw new EoIException("at " + to);
	}

	boolean isEol() throws EoIException {
		validateTo();
		return input[to] == '\n';
	}

	int findStartOfNextHeader() throws EoIException {
		while (!isEol()) 
			to++;
		to++;
		return to;
	}

	// as defined by http://tools.ietf.org/html/rfc822
	boolean isLWSP_char(int i) {
		if (i >= input.length)
			return false;
		byte x = input[i];
		return x == ' ' || x == '\t';
	}

	String nextHeader() throws EoIException, LastHeaderException, HttpParseException {
		from = to;
		while (true) {
			while (!isEol())
				to++;

			// if next line starts with LWSP_char then it is an extension line (line folding) 
			if (!isLWSP_char(to + 1))
				break;
			to++;
		}

		String header = new String(input, from, to-from).trim();
		if (header.length() > 0) {
			message.addHeader(header);
			to = findStartOfNextHeader();
		} else {
			to = findStartOfNextHeader();
			throw new LastHeaderException("");
		}
		return header;
	}

	public String getNextHeader() throws HttpParseException {
		try {
			String hdr = nextHeader();
			return hdr;
		} catch (EoIException e) {
			return null;
		} catch (LastHeaderException e) {
			return null;
		}
	}

	String getPartLabel() {
		String cid = getRawContentId();
		if (cid == null)
			return "No Part: ";
		else
			return "Part " + cid + ": ";
	}

	byte[] subarray(byte[] in, int offset) {
		int newsize = in.length - offset;
		byte[] out = new byte[newsize];
		for (int i=0; i<newsize; i++)
			out[i] = in[offset + i];
		return out;
	}
	
	void parseHeadersAndBody() throws HttpParseException, ParseException  {
		try {
			while (true)
				nextHeader();
		} catch (EoIException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			// end of input - no body
			message.setBody(new byte[0]);
		} catch (LastHeaderException e) {
			message.body = subarray(input, to); //input.substring(to);
		}

		String contentTypeString = message.getHeader("content-type");
		HttpHeader contentTypeHeader = new HttpHeader(contentTypeString);

		charset = contentTypeHeader.getParam("charset");
		if (charset == null || charset.equals("")) {
			charset = "UTF-8";
			if (er != null) {
				er.detail(getPartLabel() + "No CharSet found in Content-Type header, assuming " + charset);
				er.detail(getPartLabel() + "Content-Type header is " + contentTypeString);
			}
		} else {
			if (er != null) {
				er.detail(getPartLabel() + "CharSet is " + charset);
				er.detail(getPartLabel() + "Content-Type header is " + contentTypeString);
			}
		}

	}

	public boolean isTypicalMessage() {
		return message.headers.size() > 0 && message.body.length > 0;
	}





}


