package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valregmsg.message.StoredDocumentInt;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class StoredDocument implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public String pathToDocument;
	public String uid;
	public String mimeType;
	public String charset;
	public String hash;
	public String size;
	
	transient public String cid;
	
	transient public byte[] content;
	
	public StoredDocument(StoredDocumentInt sdi) {
		pathToDocument = sdi.pathToDocument;
		uid = sdi.uid;
		mimeType = sdi.mimeType;
		charset = sdi.charset;
		hash = sdi.hash;
		size = sdi.size;
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
		return Io.bytesFromFile(f);
	}

}
