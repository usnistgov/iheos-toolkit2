package gov.nist.toolkit.session.shared.gov.nist.toolkit.session.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Reference to a resource in the EC/dataset collection
 * which is referenced by File Installation.instance().datasets();
 * path is an offset from that directory
 */
public class DatasetElement implements Serializable, IsSerializable {
    private String path;

    public DatasetElement() {}

    public DatasetElement(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isValid() {
        return getParts().length == 3;
    }

    public String[] getParts() {
        return path.split("/");
    }

    @Override
    public String toString() { return path; }

}
