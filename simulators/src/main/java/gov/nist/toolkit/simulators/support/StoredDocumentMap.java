package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoredDocumentMap {
	public List<StoredDocument> docs = new ArrayList<StoredDocument>();

	/**
	 * Build StoredDocumentMap from RetInfo struct.  String in map is uid.
	 * @param retmap
	 */
	public StoredDocumentMap(Map<String, RetrievedDocumentModel> retmap) {
		for (String uid : retmap.keySet()) {
			RetrievedDocumentModel ri = retmap.get(uid);
			
			StoredDocument doc = new StoredDocument();

			doc.setPathToDocument(null);
			doc.setUid(uid);
			doc.setMimeType(ri.getContent_type());
			doc.setCharset(null);
			doc.setHash(ri.getHash());
			doc.size = Integer.toString(ri.getSize());
			doc.cid = mkCid();
			doc.content = ri.getContents();
				
			docs.add(doc);
		}
	}

    public StoredDocumentMap() {}

    public void addDoc(StoredDocument sd) { docs.add(sd); }

    public List<StoredDocument> getDocs() { return docs; }

	int id = 1;
	String mkCid() {
		return "doc" + id++ + "@toolkit.info";
	}

}
