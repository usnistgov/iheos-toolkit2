package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Definition of a Document contents.
 */
@XmlRootElement
public class Document {
    String mimeType;
    byte[] contents;

    public Document() {}

    public Document(String mimeType, byte[] contents) {
        this.mimeType = mimeType;
        this.contents = contents;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
