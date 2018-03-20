package gov.nist.toolkit.fhir.simulators.support

import gov.nist.toolkit.fhir.simulators.sim.rep.RepIndex
import gov.nist.toolkit.results.client.DocumentEntryDetail
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.valregmsg.message.StoredDocumentInt
import groovy.transform.TypeChecked

/**
 * Internal pathToDocument is relative.  All calls to this class pass absolute path.
 */
@TypeChecked
public class StoredDocument implements Serializable {

	
	private static final long serialVersionUID = 1L;

   String pathToDocument;  // relative
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

	transient private RepIndex repIndex = null;

	public StoredDocument() {
	}

	public StoredDocument(RepIndex repIndex, StoredDocumentInt sdi) {
		this.repIndex = repIndex
		assert repIndex
        assert sdi
		if (sdi.pathToDocument)
			pathToDocument = repIndex.getRelativePath(new File(sdi.pathToDocument).toPath());
		uid = sdi.uid;
		mimeType = sdi.mimeType;
		charset = sdi.charset;
		hash = sdi.hash;
		size = sdi.size;
		content = sdi.content;
	}

	public StoredDocument(RepIndex repIndex, String pathToDocument, String uid) {
		assert repIndex
		this.repIndex = repIndex
		this.pathToDocument = repIndex.getRelativePath(new File(pathToDocument).toPath());
		this.uid = uid;
	}

	void setPathToDocument(String pathToDocument) {
		if (repIndex)
        	this.pathToDocument = repIndex.getRelativePath(new File(pathToDocument).toPath());
		else
			this.pathToDocument = pathToDocument // deal with it later?
    }

	public StoredDocumentInt getStoredDocumentInt() {
		StoredDocumentInt sdi = new StoredDocumentInt();
		
		sdi.pathToDocument = repIndex.getAbsolutePath(new File(pathToDocument).toPath());
		sdi.uid = uid;
		sdi.mimeType = mimeType;
		sdi.charset = charset;
		sdi.hash = hash;
		sdi.size = size;
		
		return sdi;
	}

	public File getFile() {
		return repIndex.getAbsolutePath(new File(pathToDocument).toPath()).toFile();
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
		assert repIndex
		assert pathToDocument
        return repIndex.getAbsolutePath(new File(pathToDocument).toPath()).toFile();
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
