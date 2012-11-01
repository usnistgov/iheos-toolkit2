package gov.nist.toolkit.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartMessage {
	List<Part> parts = new ArrayList<Part>();
	String boundary;
	String startPartId = null;

	public Map<String, byte[]> getContentMap() {  // name => part contents
		Map<String, byte[]> cmap = new HashMap<String, byte[]>();
		
		for (Part part : parts) {
			String cname = part.getContentName();
			byte[] cvalue = part.getBodyBytes();
			cmap.put(cname, cvalue);
		}
		
		return cmap;
	}
	
	public String getPartContentsById(String contentId) {
		if (contentId == null || contentId.equals(""))
			return null;
		if (contentId.startsWith("cid:"))
			contentId = contentId.substring(4);
		
		for (Part p : parts) {
			if (contentId.equals(p.getContentId()))
				return p.getBody();
		}
		return null;
	}
	
	public int getPartCount() {
		return parts.size();
	}
	
	public Part getPart(int i) {
		if (i < parts.size()) 
			return parts.get(i);
		return null;
	}
	
	public Part getPart(String id) {
		if (id == null || id.equals(""))
			return null;
		for (Part p : parts) {
			if (id.equals(p.getContentId()))
				return p;
		}
		return null;
	}

	public Part getStartPart() {
		if (startPartId != null)
			return getPart(startPartId);
		if (parts.size() > 0)
			return getPart(0);
		return null;
	}
	
	public String getStartPartId() {
		return startPartId;
	}

	public String asMessage() throws HttpParseException {
		StringBuffer buf = new StringBuffer();

		for (Part part : parts) {
			buf.append(boundary);
			buf.append("\r\n");
			buf.append(part.asMessage());
			buf.append("\r\n");
		}
		buf.append(boundary);
		buf.append("--");
		buf.append("\r\n");

		return buf.toString();
	}


}
