package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class HttpParser {
	byte[] input;
	int from;
	int to = 0;
	ErrorRecorder er = null;
	String charset = null;
	HttpMessage message = new HttpMessage();
	MultipartParser multiparser;
	boolean appendixV = true;
	static final Logger logger = Logger.getLogger(HttpParser.class);

	
	public MultipartParser getMultipartParser() {
		return multiparser;
	}
	
	public HttpMessage getHttpMessage() {
		return message;
	}
	
	public void setBody(String body) {
		message.bodyBytes = body.getBytes();
	}
	
	public byte[] getBody() {
		return message.bodyBytes;
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

	public String getHeaderValue(String headerName)  {
		return getHeaderValue(headerName, 0);
	}
	
	public String getHeaderValue(String headerName, int i)  {
		String hdrStr = message.getHeader(headerName, i);
		if (hdrStr == null || hdrStr.equals(""))
			return null;
		try {
			HttpHeader hh = new HttpHeader(hdrStr);
			return hh.getValue();
		} catch (Exception e) {
			return null;
		}
	}
	
	public HttpHeader getHeader(String headerName) {
		return getHeader(headerName, 0);
	}
	
	public HttpHeader getHeader(String headerName, int i) {
		String hdrStr = message.getHeader(headerName, i);
		if (hdrStr == null || hdrStr.equals(""))
			return null;
		try {
			HttpHeader hh = new HttpHeader(hdrStr);
			return hh;
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

	public MultipartMessage getMultipart() {
		return message.multipart;
	} 

	public void setErrorRecorder(ErrorRecorder er) {
		this.er = er;
	}

	public HttpParser() {

	}

	public HttpParser(HttpServletRequest request) throws IOException, HttpParseException {
		// This is the default toString() since it shows an object id (helps understand recursion)
		logger.debug("new HttpParser(" + this.toString() + ")");
		init(request);
	}

	public HttpParser(HttpServletRequest request, boolean appendixV) throws IOException, HttpParseException {
		logger.debug("new HttpParser(" + this.toString() + ")");
		this.appendixV = appendixV;
		init(request);
	}

	public HttpParser(HttpServletRequest request, ErrorRecorder er) throws IOException, HttpParseException {
		logger.debug("new HttpParser(" + this.toString() + ")");
		this.er = er;
		init(request);
	}

	public void init(HttpServletRequest request) throws IOException, HttpParseException {
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String name = e.nextElement();
			String value = request.getHeader(name);
			message.addHeader(name, value);
		}
		
		if (er == null) {
			// caller must not be interested in the ErrorRecorder results
			er = new TextErrorRecorder();
		}

		message.bodyBytes = Io.getBytesFromInputStream(request.getInputStream());

		tryMultipart();
		
		er.detail("Message " + ((isMultipart()) ? "is" : "is not" ) + " a multipart");
	}

	public HttpParser(byte[] msg) throws HttpParseException, HttpHeaderParseException, ParseException {
		logger.debug("new HttpParser(" + this.toString() + ")");
		er = null;
		init(msg, null, er);
	}

	public HttpParser(byte[] msg, ErrorRecorder er) throws HttpParseException, HttpHeaderParseException, ParseException  {
		logger.debug("new HttpParser(" + this.toString() + ")");
		this.er = er;
		init(msg, null, er);
	}
	
	public HttpParser(byte[] msg, ErrorRecorder er, boolean appendixV) throws HttpParseException, HttpHeaderParseException, ParseException  {
		logger.debug("new HttpParser(" + this.toString() + ")");
		this.er = er;
		this.appendixV = appendixV;
		init(msg, null, er);
	}
	
	public void init(byte[] msg, HttpMessage hmessage, ErrorRecorder er) throws ParseException, HttpParseException  {
		input = msg;
		if (hmessage != null)
			message = hmessage;
		parse();
		tryMultipart();

		if (isMultipart()) {
			er.detail("Multipart parsed");
			for (Enumeration<String> en=message.getHeaderNames(); en.hasMoreElements(); ) {
				String hdr = en.nextElement();
				String hdrVal = message.getHeader(hdr);
				System.out.println(hdrVal);
			}
		} else {
			er.detail("Simple Part parsed");
			for (Enumeration<String> en=message.getHeaderNames(); en.hasMoreElements(); ) {
				String hdr = en.nextElement();
				String hdrVal = message.getHeader(hdr);
				System.out.println(hdrVal);
			}
		}
	}

	public void tryMultipart()  {
		try {
			multiparser = new MultipartParser(this, er, appendixV);
			message.multipart = multiparser.message;
			logger.debug("HttpParser(" + this.toString() + ") - isMultipart=" + isMultipart() );
		} catch (ParseException e) {
			// not a multipart
			System.out.println(ExceptionUtil.exception_details(e));
			message.multipart = null;
		} catch (HttpHeaderParseException e) {
			// not a multipart
			System.out.println(ExceptionUtil.exception_details(e));
			message.multipart = null;
		} catch (HttpParseException e) {
			// not a multipart
			System.out.println(ExceptionUtil.exception_details(e));
			message.multipart = null;
		} catch (Throwable e) {
			System.out.println(ExceptionUtil.exception_details(e));
			message.multipart = null;
		}
	}

	public void parse() throws ParseException, HttpParseException  {
//		if (parsed)
//			return;
//		parsed = true;
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

		byte[] headerBytes = new byte[to-from];
		for (int j=from, i=0; j<to; j++, i++)
			headerBytes[i] = input[j];
//		String header = input.substring(from, to).trim();
		if (from == to) {
			to = findStartOfNextHeader();
			throw new LastHeaderException("");
		}
		else  {
			String header = new String(headerBytes);
			if (header.equals("\r")) {
				if (isEol())
					findStartOfNextHeader();
				throw new LastHeaderException("");
			}
			header = header.trim();
			message.addHeader(header);
			to = findStartOfNextHeader();
			er.detail("Header: " + header);
			return header;
		}
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
			cid = "Unlabeled";
		return "Part (" + cid + "): ";
	}

	void parseHeadersAndBody() throws  ParseException, HttpParseException {
		try {
			while (true)
				nextHeader();
		} catch (EoIException e) {
			System.out.println(ExceptionUtil.exception_details(e));
			// end of input - no body
			message.setBody("");
		} catch (LastHeaderException e) {
			int last = input.length;
			message.bodyBytes = new byte[last-to];
			for (int i=0; i<last-to; i++)
				 message.bodyBytes[i] = input[to+i];
		}

		String contentTypeString = message.getHeader("content-type");
		logger.debug("HttpParser(" + this.toString() + ") - content-type=" + contentTypeString);

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
		return message.headers.size() > 0 && message.bodyBytes.length > 0;
	}




}
