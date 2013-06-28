package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;

import java.util.List;

import org.apache.log4j.Logger;

public class MultipartParser {
	HttpParser hp;
	HttpHeader contentTypeHeader;
	ErrorRecorder er = null;
	boolean appendixV = true;
	MultipartMessage message = new MultipartMessage();
	static final Logger logger = Logger.getLogger(HttpParser.class);

	public MultipartParser(HttpParser hp) throws ParseException, HttpHeaderParseException, HttpParseException {
		logger.debug("new MultipartParser(" + this.toString() + ")");
		this.hp = hp;
		parse();
	}

	public MultipartParser(HttpParser hp, ErrorRecorder er, boolean appendixV) throws ParseException, HttpHeaderParseException, HttpParseException {
		logger.debug("new MultipartParser(" + this.toString() + ")");
		this.hp = hp;
		this.er = er;
		this.appendixV = appendixV;
		parse();
	}
	
	MultipartParser() {}
	
	public MultipartMessage getMultipartMessage() {
		return message;
	}

	public void addPart(Part p) {
		message.parts.add(p);
	}

	public String getStartPartId() {
		return message.startPartId;
	}

	public Part getStartPart() {
		if (message.startPartId != null)
			return getPart(message.startPartId);
		if (message.parts.size() > 0)
			return getPart(0);
		return null;
	}

	public List<Part> getParts() {
		return message.parts;
	}

	public int getPartCount() {
		return message.parts.size();
	}

	public Part getPart(int i) {
		if (i >= message.parts.size())
			return null;
		return message.parts.get(i);
	}

	public Part getPart(String id) {
		if (id == null || id.equals(""))
			return null;
		for (Part p : message.parts) {
			if (id.equals(p.getContentId()))
				return p;
		}
		return null;
	}
	
	int indexOf(byte[] input, String target, int from) {
		byte[] targt = target.getBytes();
		for (int offset=0; ; offset++) {
			if (from+offset+targt.length > input.length)
				return -1;
			boolean found = true;
			for (int i=0; i<targt.length; i++) {
				if (input[from+offset+i] != targt[i]) {
					found = false;
					break;
				}
			}
			if (found)
				return from+offset;
		}
	}
	
	static public void main(String[] args) {
		MultipartParser mp = new MultipartParser();
		
		assert mp.indexOf("student".getBytes(), "stud", 0) == 0;
		assert mp.indexOf("student".getBytes(), "tud", 0) == 1;
		assert mp.indexOf("student".getBytes(), "udent", 1) == 2;
		assert mp.indexOf("student".getBytes(), "udent", 2) == 2;
		assert mp.indexOf("student".getBytes(), "stid", 0) == -1;
		
		assert new String(mp.substring("student".getBytes(), 0, 3)).equals("stu");
		assert new String(mp.substring("student".getBytes(), 1, 3)).equals("tu");
		assert new String(mp.substring("student".getBytes(), 2, 5)).equals("ude");
		assert new String(mp.substring("student".getBytes(), 0, 7)).equals("student");
	}
	
	byte[] substring(byte[] input, int from, int to) {
		int size = to - from;
		byte[] out = new byte[size];
		
		for (int i=0; i<size; i++)
			out[i] = input[from+i];
		
		return out;
	}

	public void parse() throws ParseException, HttpHeaderParseException, HttpParseException {
//		if (!isMultipart())
//			throw new ParseException("Not a Multipart");
		boolean multi = isMultipart();
		message.boundary = contentTypeHeader.getParam("boundary");
		logger.debug("MultipartParser(" + this.toString() + ") - boundary = " + message.boundary);
		er.detail(contentTypeHeader.asString());
		er.detail("boundary = " + message.boundary);
		if (message.boundary == null || message.boundary.equals("")) {
			message = null;
			return;
		}
			//throw new ParseException("No boundary");

		String pboundary = "--" + message.boundary;
		byte[] body = hp.message.getBodyBytes();
		int from=0;
		int to=0;
		while(true) {
			from = indexOf(body, pboundary, to);
			if (from == -1) {
				if (message.parts.size() == 0 && er != null)
					er.err(XdsErrorCode.Code.NoCode, "Multipart boundary [" + pboundary + "] not found in message body", this, "http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html");
				break;
			}
			//System.out.println("***************\nfrom is:\n" + body.substring(from));
			from = afterBoundary(body, from);
			if (from == -1)
				break;
			//System.out.println("***************\nfrom2 is:\n" + body.substring(from));
			to = indexOf(body, pboundary, from);
			if (to == -1)
				break;
			//System.out.println("***************\nto is:\n" + body.substring(to));

			PartParser pp = new PartParser(substring(body, from, to), er, appendixV);
			message.parts.add(pp.part);
		}

		if (message.parts.size() == 0) {
			if (er != null)
				er.err(XdsErrorCode.Code.NoCode, "No Parts found in Multipart", this, "");
			return;
		}

		String contentTypeString = hp.message.getHeader("content-type");
		HttpHeader contentTypeHeader = new HttpHeader(contentTypeString);
		message.startPartId = contentTypeHeader.getParam("start");
		if (message.startPartId == null || message.startPartId.equals("")) {
			if (er != null)
				er.detail("No start parameter found on Content-Type header - using first Part");
			Part startPart = message.parts.get(0);
			message.startPartId = startPart.getContentId();
		} else {
			if (!PartParser.isWrappedIn(message.startPartId, "<", ">")) {
				if (er != null)
					er.err(XdsErrorCode.Code.NoCode, "Content-Type header has start parameter but it is not wrapped in <   >", this, "http://www.w3.org/TR/2005/REC-xop10-20050125/  Example 2");
			} else {
				message.startPartId = PartParser.unWrap(message.startPartId);
			}
		}
		if (er != null)
			er.detail("Start Part identified as [" + message.startPartId + "]");

		if (appendixV) {
			String contentTypeValue = contentTypeHeader.getValue();
			if (contentTypeValue == null) contentTypeValue = "";
			if (!"multipart/related".equals(contentTypeValue.toLowerCase()))
				if (er != null) {
					er.err(XdsErrorCode.Code.NoCode, "Content-Type header must have value " + "multipart/related" + " - found instead " + contentTypeValue, this, "http://www.w3.org/TR/soap12-mtom - Section 3.2");
				} else {
					throw new HttpParseException("Content-Type header must have value " + "multipart/related" + " - found instead " + contentTypeValue);
				}
			String type = contentTypeHeader.getParam("type");
			if (type == null) type = "";
			if (!"application/xop+xml".equals(type.toLowerCase()))
				if (er != null) {
					er.err(XdsErrorCode.Code.NoCode, "Content-Type header must have type parameter equal to application/xop+xml - found instead " + type + ". Full content-type header was " + contentTypeString, this, "http://www.w3.org/TR/soap12-mtom - Section 3.2");
				} else {
					throw new HttpParseException("Content-Type header must have type parameter equal to application/xop+xml - found instead " + type + ". Full content-type header was " + contentTypeString);
				}
		}

	}

	int afterBoundary(byte[] body, int at) {
		int x = indexOf(body, "\n", at);
		if (x == -1)
			return -1;
		x++;
		if (x >= body.length)
			return -1;
		return x;
	}

	boolean isMultipart() {
		String name;
		try {
			contentTypeHeader = new HttpHeader(hp.message.getHeader("Content-Type"));
		} catch (ParseException e) {
			return false;
		}
		name = contentTypeHeader.getName();
		if (name == null)
			return false;
		// Value is name:value combo
		String val = contentTypeHeader.getValue().toLowerCase().trim();
		boolean ismulti = val != null && val.indexOf("multipart") != -1;
		return ismulti;
	}



}
