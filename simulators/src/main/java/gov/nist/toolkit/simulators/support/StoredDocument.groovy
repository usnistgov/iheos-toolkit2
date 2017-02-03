package gov.nist.toolkit.simulators.support

import gov.nist.toolkit.results.client.DocumentEntryDetail
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.valregmsg.message.StoredDocumentInt
import groovy.transform.TypeChecked

@TypeChecked
public class StoredDocument implements Serializable {

	
	private static final long serialVersionUID = 1L;

   String pathToDocument;
	String uid;
	String mimeType;
	String charset;
	String hash;
	/**
	 * Document Entry Details
	 */
	DocumentEntryDetail entryDetail;
	/**
	 * Local StoredDocument list item identifier.
	 */
	int id;

    public String size;
	
	transient public String cid;
	
	transient public byte[] content;
	
	public StoredDocument(StoredDocumentInt sdi) {
        assert sdi
		pathToDocument = sdi.pathToDocument;
		uid = sdi.uid;
		mimeType = sdi.mimeType;
		charset = sdi.charset;
		hash = sdi.hash;
		size = sdi.size;
		content = sdi.content;
	}

    void setPathToDocument(String pathToDocument) {
        this.pathToDocument = pathToDocument
    }

	public StoredDocumentInt getStoredDocumentInt() {
		StoredDocumentInt sdi = new StoredDocumentInt();
		
		sdi.pathToDocument = pathToDocument;
		sdi.uid = uid;
		sdi.mimeType = mimeType;
		sdi.charset = charset;
		sdi.hash = hash;
		sdi.size = size;
		
		return sdi;
	}

	public File getFile() {
		return new File(pathToDocument);
	}
	
	public StoredDocument() {
		
	}
	
	public StoredDocument(String pathToDocument, String uid) {
		this.pathToDocument = pathToDocument;
		this.uid = uid;
	}
	
	public void setMimetype(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setSize(String size) {
		this.size = size;
	}
		
	public File getPathToDocument() {
        return new File(pathToDocument);
	}
	
	public byte[] getDocumentContents() throws IOException {
		File f = getPathToDocument();
		setContent(Io.bytesFromFile(f));
		return content;
	}

    public byte[] getContent() {
        if (content) return content;
        if (pathToDocument) return getDocumentContents();
        return null;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

	DocumentEntryDetail getEntryDetail() {
		return entryDetail
	}

	void setEntryDetail(DocumentEntryDetail entryDetail) {
		this.entryDetail = entryDetail
	}

	int getId() {
		return id
	}

	void setId(int id) {
		this.id = id
	}
}
