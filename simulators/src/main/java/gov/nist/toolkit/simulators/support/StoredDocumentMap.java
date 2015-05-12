package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.testengine.RetInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoredDocumentMap {
	public List<StoredDocument> docs = new ArrayList<StoredDocument>();

	/**
	 * Build StoredDocumentMap from RetInfo struct.  String in map is uid.
	 * @param retmap
	 */
	public StoredDocumentMap(Map<String, RetInfo> retmap) {
		for (String uid : retmap.keySet()) {
			RetInfo ri = retmap.get(uid);
			
			StoredDocument doc = new StoredDocument();

			doc.pathToDocument = null;
			doc.uid = uid;
			doc.mimeType = ri.getContent_type();
			doc.charset = null;
			doc.hash = ri.getHash();
			doc.size = Integer.toString(ri.getSize());
			doc.cid = mkCid();
			doc.content = ri.getContents();
				
			docs.add(doc);
		}
	}

	int id = 1;
	String mkCid() {
		return "doc" + id++ + "@toolkit.info";
	}

}
