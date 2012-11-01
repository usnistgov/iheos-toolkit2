package gov.nist.toolkit.http;

import gov.nist.toolkit.http.HttpMessage.Header;
import gov.nist.toolkit.http.HttpMessage.HeaderNamesEnumeration;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpMessageBa {

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
		
	}
	

	List<Header> headers = new ArrayList<Header>();
	byte[] body;
	MultipartMessageBa multipart;

	public HttpMessageBa() {}

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

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
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
			bodybuf.append(body);
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
		String lcHeaderName = headerName.toLowerCase();
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

	public String getHeader(String headerName) {
		if (headerName == null)
			return null;
		String lcHeaderName = headerName.toLowerCase();
		
		for (int i=0; i<headers.size(); i++) {
			if (lcHeaderName.equals(headers.get(i).lcname))
				return headers.get(i).name + ": " + headers.get(i).value;
		}
		return null;
	}

	public String getHeaderValue(String headerName) {
		if (headerName == null)
			return null;
		String lcHeaderName = headerName.toLowerCase();
		
		for (int i=0; i<headers.size(); i++) {
			if (lcHeaderName.equals(headers.get(i).lcname))
				return headers.get(i).value;
		}
		return null;
	}
	
	public String getContentTransferEncoding() {
		return getHeaderValue("content-transfer-encoding");
	}
	
	public String getContentType() {
		String v = getHeaderValue("content-type");
		String parts[] = v.split(";");
		if (parts.length == 1)
			return v;
		return parts[0];
	}
	
	public String getCharset() {
		String v = getHeaderValue("content-type");
		String parts[] = v.split(";");
		if (parts.length == 1)
			return null;
		String vals[] = parts[1].split("=");
		if (vals.length == 2)
			return vals[1];
		return null;
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
