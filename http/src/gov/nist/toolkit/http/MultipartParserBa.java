package gov.nist.toolkit.http;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;

import java.util.List;

public class MultipartParserBa {
		HttpParserBa hp;
		HttpHeader contentTypeHeader;
		ErrorRecorder er = null;
		boolean appendixV = true;
		MultipartMessageBa message = new MultipartMessageBa();

		public MultipartParserBa(HttpParserBa hp) throws ParseException, HttpHeaderParseException, HttpParseException {
			this.hp = hp;
			parse();
		}

		public MultipartParserBa(HttpParserBa hp, ErrorRecorder er, boolean appendixV) throws ParseException, HttpHeaderParseException, HttpParseException {
			this.hp = hp;
			this.er = er;
			this.appendixV = appendixV;
			parse();
		}
		
		public MultipartMessageBa getMultipartMessage() {
			return message;
		}

		public void addPart(PartBa p) {
			message.parts.add(p);
		}

		public String getStartPartId() {
			return message.startPartId;
		}

		public PartBa getStartPart() {
			if (message.startPartId != null)
				return getPart(message.startPartId);
			if (message.parts.size() > 0)
				return getPart(0);
			return null;
		}

		public List<PartBa> getParts() {
			return message.parts;
		}

		public int getPartCount() {
			return message.parts.size();
		}

		public PartBa getPart(int i) {
			if (i >= message.parts.size())
				return null;
			return message.parts.get(i);
		}

		public PartBa getPart(String id) {
			if (id == null || id.equals(""))
				return null;
			for (PartBa p : message.parts) {
				if (id.equals(p.getContentId()))
					return p;
			}
			return null;
		}
		
		int indexOf(byte[] in, char b, int startingAt) {
			byte[] b1 = new byte[1];
			b1[0] = (byte) b;
			return indexOf(in, b1, startingAt);
		}
		
		int indexOf(byte[] in, byte b[], int startingAt) {
			for (int start=startingAt; start<in.length; start++) {
				boolean found = true;
				for (int i=0; i<b.length; i++) {
					if (start+i >= in.length)
						return -1;
					if (in[start+i] != b[i]) {
						found = false;
						break;
					}
				}
				if (found)
					return start;
			}
			return -1;
		}
		
		byte[] subarray(byte[] in, int from, int to) {
			byte[] out = new byte[to-from];
			for (int i=0; i<to-from; i++) {
				out[i] = in[from+i];
			}
			return out;
		}
		
		boolean isWhite(byte b) {
			return (b == '\n' || b == '\t' || b == ' ');
		}
		
		byte[] trim(byte[] in) {
			int i=in.length - 1;
			for (  ; i>=0; i--) {
				if (!isWhite(in[i]))
					break;
			}
			if (i == in.length - 1)
				return in;
			if (i == -1)
				return new byte[0];
			return subarray(in, 0, i+1);
		}

		public void parse() throws ParseException, HttpHeaderParseException, HttpParseException {
			if (!isMultipart())
				throw new ParseException("Not a Multipart");
			message.boundary = contentTypeHeader.getParam("boundary");
			
			if (er != null)
				er.detail("boundary string = " + message.boundary);
			
			if (message.boundary == null || message.boundary.equals(""))
				throw new ParseException("No boundary");

			String pboundary = "--" + message.boundary;
			byte[] body = hp.message.getBody();
			int from=0;
			int to=0;
			while(true) {
				from = indexOf(body, pboundary.getBytes(), to);
				if (from == -1) {
					if (message.parts.size() == 0 && er != null)
						er.err(XdsErrorCode.Code.NoCode, "Multipart boundary [" + pboundary + "] not found in message body", this, "http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html");
					break;
				}
				from = afterBoundary(body, from);
				if (from == -1)
					break;
				to = indexOf(body, pboundary.getBytes(), from);
				if (to == -1)
					break;

				PartParserBa pp = new PartParserBa(trim(subarray(body, from, to)), er, appendixV);
				message.parts.add(pp.part);
			}

			if (message.parts.size() == 0) {
				if (er != null)
					er.err(XdsErrorCode.Code.NoCode, "No Parts found in Multipart", this, "");
				return;
			} else
				if (er != null)
					er.detail("multipart has " + message.parts.size() + " parts");

			String contentTypeString = hp.message.getHeader("content-type");
			HttpHeader contentTypeHeader = new HttpHeader(contentTypeString);
			message.startPartId = contentTypeHeader.getParam("start");
			if (message.startPartId == null || message.startPartId.equals("")) {
				if (er != null)
					er.detail("No start parameter found on Content-Type header - using first Part");
				PartBa startPart = message.parts.get(0);
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
				if (er != null)
					er.detail("ContentType is " + contentTypeValue);
				if (!"multipart/related".equals(contentTypeValue.toLowerCase()))
					if (er != null) {
						er.err(XdsErrorCode.Code.NoCode, "Content-Type header must have value " + "multipart/related" + " - found instead " + contentTypeValue, this, "http://www.w3.org/TR/soap12-mtom - Section 3.2");
					} else {
						throw new HttpParseException("Content-Type header must have value " + "multipart/related" + " - found instead " + contentTypeValue);
					}
				String type = contentTypeHeader.getParam("type");
				if (er != null)
					er.detail("ContentType type param is " + type);
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
			int x = indexOf(body, '\n', at);
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
			String val = contentTypeHeader.getValue().toLowerCase().trim();
			return val != null && val.startsWith("multipart");
		}



	}

