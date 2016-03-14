package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.Document;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Definition of a Document contents.
 */
@XmlRootElement
public class DocumentResource implements Document {
    String mimeType;
    byte[] contents;

    public DocumentResource() {}

    public DocumentResource(String mimeType, byte[] contents) {
        this.mimeType = mimeType;
        this.contents = contents;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
