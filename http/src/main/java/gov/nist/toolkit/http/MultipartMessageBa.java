package gov.nist.toolkit.http;

import java.util.ArrayList;
import java.util.List;

public class MultipartMessageBa {
	List<PartBa> parts = new ArrayList<PartBa>();
	String boundary;
	String startPartId = null;

	
	public byte[] getPartContentsById(String contentId) {
		if (contentId == null || contentId.equals(""))
			return null;
		if (contentId.startsWith("cid:"))
			contentId = contentId.substring(4);
		
		for (PartBa p : parts) {
			if (contentId.equals(p.getContentId()))
				return p.getBody();
		}
		return null;
	}
	
	public int getPartCount() {
		return parts.size();
	}
	
	public PartBa getPart(int i) {
		if (i < parts.size()) 
			return parts.get(i);
		return null;
	}
	
	public PartBa getPart(String id) {
		if (id == null || id.equals(""))
			return null;
		for (PartBa p : parts) {
			if (id.equals(p.getContentId()))
				return p;
		}
		return null;
	}

	public PartBa getStartPart() {
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

		for (PartBa part : parts) {
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
