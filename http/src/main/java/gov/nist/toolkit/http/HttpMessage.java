package gov.nist.toolkit.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpMessage  {
	
	class Header {
		String name;
		String value;
		String lcname;
		
		Header() {}
		Header(String name, String value) {
			this.name = name;
			this.lcname = name.toLowerCase();
			this.value = value;
		}
		
		public String toString() {
			return name + ": " + value;
		}
		
		public String getHeaderField(String fieldName) {
			String[] parts = value.split(";");
			for (int i=0; i<parts.length; i++) {
				String[] nameval = parts[i].split("=");
				if (nameval.length != 2)
					continue;
				if (nameval[0].trim().equals(fieldName)) {
					String val = nameval[1].trim();
					if (val.charAt(0) == '"')
						val = val.substring(1);
					if (val.charAt(val.length()-1) == '"')
						val = val.substring(0, val.length()-1);
					return val;
				}
			}
			return "";
		}
		
	}
	

	List<Header> headers = new ArrayList<Header>();
//	String body = "";
	byte[] bodyBytes;
	MultipartMessage multipart;
	
	public String getContentName() {
		Header contentDisposition = getHeaderObject("Content-Disposition");
		String value = contentDisposition.getHeaderField("name");
		return value;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (Header h : headers) 
			buf.append(h.name).append(": ").append(h.value).append("\r\n");
		buf.append("\r\n");
		buf.append(new String(bodyBytes));
		
		return buf.toString();
	}

	public HttpMessage() {}

	public void setHeaderMap(Map<String, String> hdrs) {		
		for (String key : hdrs.keySet()) {
			String val = hdrs.get(key);
			
			Header h = new Header(key, val);
			headers.add(h);
		}
	}

	public List<String> getHeaders() {
		List<String> hs = new ArrayList<String>();
		for (Header h : headers) {
			hs.add(h.name + ": " + h.value);
		}
		return hs;
	}

	public String getBody() {
		return new String(bodyBytes);
	}
	
	public byte[] getBodyBytes() {
		return bodyBytes;
	}

	public void setBody(String body) {
		this.bodyBytes = body.getBytes();
	}
	
	public String getHeadersAsString() {
		StringBuffer buf = new StringBuffer();
		
		for (String hdr : getHeaders()) {
			buf.append(hdr);
			buf.append("\r\n");
		}
		
		return buf.toString();
	}

	public String asMessage() throws HttpParseException {
		StringBuffer buf = new StringBuffer();
		StringBuffer bodybuf = new StringBuffer();

		if (multipart == null)
			bodybuf.append(bodyBytes);
		else
			bodybuf.append(multipart.asMessage());


		// fix Content-Length header
		removeHeader("Content-Length");
		if (bodybuf.length() != 0)
			addHeader("Content-Length: " + bodybuf.length());

		for (String hdr : getHeaders()) {
			buf.append(hdr);
			buf.append("\r\n");
		}
		buf.append("\r\n");

		buf.append(bodybuf);

		return buf.toString();
	}

	public boolean removeHeader(String headerName) {
		boolean changed = false;
		
		for (int i=0; i<headers.size(); i++) {
			Header h = headers.get(i);
			if (h.lcname.equals(headerName)) {
				headers.remove(i);
				i--;
				changed = true;
			}
		}
		return changed;
		
	}

	/**
	 * Get first header with name headerName
	 * @param headerName
	 * @return header string (name: value)
	 */
	public String getHeader(String headerName) {
		return getHeader(headerName, 0);
//		if (headerName == null)
//			return null;
//		String lcHeaderName = headerName.toLowerCase();
//		
//		for (int i=0; i<headers.size(); i++) {
//			if (lcHeaderName.equals(headers.get(i).lcname))
//				return headers.get(i).name + ": " + headers.get(i).value;
//		}
//		return null;
	}

	/**
	 * Get the i'th header with name headerName
	 * @param headerName
	 * @param i - starts counting with 0 (as first)
	 * @return header string (name: value)
	 */
	public String getHeader(String headerName, int i) {
		if (headerName == null)
			return null;
//		String lcHeaderName = headerName.toLowerCase();
		
		int which = 0;
		for (int j=0; j<headers.size(); j++) {
			if (headerName.equalsIgnoreCase(headers.get(j).lcname)) {
				if (which == i)
					return headers.get(j).name + ": " + headers.get(j).value;
				which++;
			}
		}
		return null;
	}

	public String getHeaderValue(String headerName) {
		return getHeaderValue(headerName, 0);
//		if (headerName == null)
//			return null;
//		String lcHeaderName = headerName.toLowerCase();
//		
//		for (int i=0; i<headers.size(); i++) {
//			if (lcHeaderName.equals(headers.get(i).lcname))
//				return headers.get(i).value;
//		}
//		return null;
	}
	
	public Header getHeaderObject(String name) {
		String lcName = name.toLowerCase();
		for (Header h : headers) {
			if (lcName.equals(h.lcname))
				return h;
		}
		return null;
	}
	
	public String getHeaderValue(String headerName, int i) {
		if (headerName == null)
			return null;
		String lcHeaderName = headerName.toLowerCase();
		
		int which = 0;
		for (int j=0; j<headers.size(); j++) {
			if (lcHeaderName.equals(headers.get(j).lcname)) {
				if (which == i)
					return headers.get(j).value;
				which++;
			}
		}
		return null;
	}
	
	public String getContentTransferEncoding() {
		return getHeaderValue("content-transfer-encoding");
	}
	
	public String getContentType() {
		return getHeaderValue("content-type");
	}

	public void addHeader(String name, String value) {
		Header h = new Header(name, value);
		headers.add(h);
	}

	public void addHeader(String header) throws HttpParseException {
		String[] parts = header.split(":",2);
		if (parts.length != 2) {
			if (!header.startsWith("POST"))
				throw new HttpParseException("Header [" + header + "] does not parse");
			return;
		}
		Header h = new Header(parts[0].trim(), parts[1].trim());
		headers.add(h);
	}
	
	public Enumeration<String> getHeaderNames() {
		List<String> names = new ArrayList<String>();
		for (Header h : headers) {
			names.add(h.name);
		}
		return new HeaderNamesEnumeration(names.iterator());
	}

	class HeaderNamesEnumeration implements Enumeration<String> {
		Iterator<String> it;
		
		HeaderNamesEnumeration(Iterator<String> it) {
			this.it = it;
		}

		public boolean hasMoreElements() {
			return it.hasNext();
		}

		public String nextElement() {
			return it.next();
		}

	}

}
