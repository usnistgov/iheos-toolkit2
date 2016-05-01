package gov.nist.toolkit.toolkitServicesCommon.resource.xdm;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class XdmItem {
    String path;
    byte[] content;

    public XdmItem() {}

    public XdmItem(String path, byte[] content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
