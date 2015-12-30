package gov.nist.toolkit.registrymsg.repository;

import gov.nist.toolkit.utilities.io.Sha1Bean;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class RetrievedDocumentModel {
	static Logger logger = Logger.getLogger(RetrievedDocumentModel.class);

	protected String doc_uid;
	protected String rep_uid;
	
	private byte[] contents;
	protected String content_type;
	protected String hash;
	protected String  home;
	protected int size;
    private String cid = null;
	
	protected StringBuffer errors;

	public RetrievedDocumentModel() { doc_uid = ""; rep_uid = ""; content_type = ""; hash = null; size=-1; contents = null; errors = new StringBuffer();}

	public String toString() {
		return "RetInfo:\ndoc_uid=" + doc_uid + 
		"\nrep_uid=" + rep_uid + 
		"\ncontents has " + ((contents != null) ? contents.length : "(no contents)") + 
		" bytes\nhash=" + hash + 
		"\nsize=" + size  +
                "\nhome=" + home  +
                "\ncontentType=" + content_type  +
                "\nerrors=" + errors.toString() +
		"\n";
	}

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getHome() {
		return home;
	}
	
	public void setHome(String home) {
		this.home = home;
	}

	public String getDocUid() {
		return doc_uid;
	}

	public void setDocUid(String doc_uid) {
		this.doc_uid = doc_uid;
	}

	public String getRepUid() {
		return rep_uid;
	}

	public void setRepUid(String rep_uid) {
		this.rep_uid = rep_uid;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		logger.info("Retrieve: hash is " + hash);
		this.hash = hash;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		logger.info("Retrieve size is " + size);
		this.size = size;
	}
	
	public void setSize(String size) {
		logger.info("Retrieve size is " + size);
		this.size = Integer.parseInt(size);
	}

	private String sha1(byte[] buf) throws Exception {
		Sha1Bean sb = new Sha1Bean();
		sb.setByteStream(buf);
		return sb.getSha1String();
	}

	public void setContents(byte[] data) throws Exception { 
		this.contents = data; 
		this.hash = this.sha1(data); 
		this.size = data.length; 
	}

	public byte[] getContents() { return contents; }

	public String getErrors() { return errors.toString(); }

	public void addError(String msg) { errors.append(msg + "\n"); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetrievedDocumentModel that = (RetrievedDocumentModel) o;

        if (size != that.size) return false;
        if (doc_uid != null ? !doc_uid.equals(that.doc_uid) : that.doc_uid != null) return false;
        if (rep_uid != null ? !rep_uid.equals(that.rep_uid) : that.rep_uid != null) return false;
        if (!Arrays.equals(contents, that.contents)) return false;
        if (content_type != null ? !content_type.equals(that.content_type) : that.content_type != null) return false;
        return home != null ? home.equals(that.home) : that.home == null;

    }

    @Override
    public int hashCode() {
        int result = doc_uid != null ? doc_uid.hashCode() : 0;
        result = 31 * result + (rep_uid != null ? rep_uid.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(contents);
        result = 31 * result + (content_type != null ? content_type.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        result = 31 * result + size;
        return result;
    }
}
