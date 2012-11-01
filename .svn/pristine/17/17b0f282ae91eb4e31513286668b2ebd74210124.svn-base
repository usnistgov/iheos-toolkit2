package gov.nist.toolkit.utilities.xml;

import java.util.HashMap;

import org.apache.axiom.om.OMNamespace;

class NamespaceManager {
	HashMap<String, OMNamespace> nsmap = new HashMap<String, OMNamespace>();
	
	void add(OMNamespace ns) {
		if ("xml".equals(ns.getPrefix()))
			return;
		nsmap.put(ns.getPrefix(), ns);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (String prefix : nsmap.keySet()) {
			OMNamespace ns = nsmap.get(prefix);
			buf.append(" xmlns:").append(prefix).append("=").append("\"").append(ns.getNamespaceURI()).append("\"");
		}
		
		return buf.toString();
	}
	
}
