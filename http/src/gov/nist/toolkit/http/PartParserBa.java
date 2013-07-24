package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;

public class PartParserBa extends HttpParserBa {
	ErrorRecorder er = null;
	PartBa part = new PartBa();
	
	public PartParserBa(byte[] msg) throws HttpParseException, HttpHeaderParseException, ParseException {
		init(msg, part);
		initPart();
	}
	
	public PartParserBa(byte[] msg, ErrorRecorder er, boolean appendixV) throws HttpParseException, HttpHeaderParseException, ParseException {
		this.appendixV = appendixV;
		init(msg, part);
		initPart();
	}
	
	void initPart() throws HttpParseException {
		String contentIDHeaderString = message.getHeader("content-id");
		if (appendixV == false && (contentIDHeaderString == null || contentIDHeaderString.equals("")))
			return;
		try {
			HttpHeader contentIDHeader = new HttpHeader(contentIDHeaderString);
			part.contentID = contentIDHeader.getValue();
			if (part.contentID == null || part.contentID.equals(""))
				throw new HttpParseException("Part has no Content-ID header");
			part.contentID = part.contentID.trim();
			if (!isWrappedIn(part.contentID, "<",">")) {
				if (er != null)
					er.err(XdsErrorCode.Code.NoCode, "Part Content-ID header value must be wrapped in <   >: Content-ID is " + part.contentID, this, "http://www.w3.org/TR/2005/REC-xop10-20050125/  Example 2");
				else
					throw new HttpParseException("Part Content-ID header value must be wrapped in <   >: Content-ID is " + part.contentID);
			} else {
				part.contentID = unWrap(part.contentID);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public boolean isWrappedIn(String st, String start, String end) {
		if (st.length() < 3)
			return false;
		if (!st.startsWith(start))
			return false;
		if (!st.endsWith(end))
			return  false;
		return true;
	}
	
	static public String unWrap(String st) {
		return st.substring(1, st.length()-1);
	}
	
	public String getContentId() {
		return part.contentID;
	}

}
