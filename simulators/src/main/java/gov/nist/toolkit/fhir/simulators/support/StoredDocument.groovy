package gov.nist.toolkit.fhir.simulators.support

import gov.nist.toolkit.fhir.simulators.sim.rep.RepIndex
import gov.nist.toolkit.fhir.simulators.sim.rep.RepIndexSerializer
import gov.nist.toolkit.results.client.DocumentEntryDetail
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.valregmsg.message.StoredDocumentInt
import groovy.transform.TypeChecked

import java.nio.file.Path

/**
 * Internal pathToDocument is relative.  All calls to this class pass absolute path.
 */
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

	transient private RepIndex repIndex = null;

	// use only for serialization - repIndex needs initializing
	public StoredDocument() {
		println "Reload"
	}

	public StoredDocument(RepIndex repIndex) {
		this.repIndex = repIndex
	}

	public StoredDocument(RepIndex repIndex, StoredDocumentInt sdi) {
		this.repIndex = repIndex
//		assert repIndex
        assert sdi
		if (sdi.pathToDocument)
			setPathToDocument(sdi.pathToDocument)
			//pathToDocument = (pathType == PathType.RELATIVE) ? repIndex.getRelativePath(new File(sdi.pathToDocument).toPath()) : sdi.pathToDocument
		uid = sdi.uid;
		mimeType = sdi.mimeType;
		charset = sdi.charset;
		hash = sdi.hash;
		size = sdi.size;
		content = sdi.content;
	}

	public StoredDocument(RepIndex repIndex, String pathToDocument, String uid) {
//		assert repIndex
		this.repIndex = repIndex
		setPathToDocument(pathToDocument)
		//this.pathToDocument = repIndex.getRelativePath(new File(pathToDocument).toPath());
		this.uid = uid;
	}

	boolean isRelative() {
		repIndex != null
	}

	def setPathToDocument(String pathToDocument) {
		boolean canBeRelativized = repIndex && RepIndexSerializer.canBeRelativized(repIndex.filename, new File(pathToDocument).toPath())
		this.pathToDocument = (canBeRelativized) ? RepIndexSerializer.getRelativePath(repIndex.filename, new File(pathToDocument).toPath()) : pathToDocument
	}

	Path getPathToDocument() {
		if (pathToDocument.startsWith('/'))  // wasn't relativized
			return new File(pathToDocument).toPath()
		(repIndex) ? RepIndexSerializer.getAbsolutePath(repIndex.filename, new File(pathToDocument).toPath()) : new File(pathToDocument).toPath()
	}

	public File getFile() {
		return getPathToDocument().toFile()
		//return repIndex.getAbsolutePath(new File(pathToDocument).toPath()).toFile();
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
		
//	public File getPathToDocument() {
//		assert repIndex
//		assert pathToDocument
//        return repIndex.getAbsolutePath(new File(pathToDocument).toPath()).toFile();
//	}
	
	public byte[] getDocumentContents() throws IOException {
		File f = getPathToDocument().toFile()
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

	void setRepIndex(RepIndex repIndex) {
		assert repIndex
		boolean wasRelative = isRelative()

		this.repIndex = repIndex
		if (pathToDocument) {
			setPathToDocument(pathToDocument) // translate to RELATIVE
			println pathToDocument
		}
	}
}
