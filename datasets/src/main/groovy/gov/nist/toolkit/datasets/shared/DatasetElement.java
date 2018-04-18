package gov.nist.toolkit.datasets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Reference to a resource in the EC/dataset collection
 * which is referenced by File Installation.instance().datasets();
 * path is an offset from that directory
 */
public class DatasetElement implements Serializable, IsSerializable {
    private String name;
    private String type;
    private String file;

    public DatasetElement() {}

    public DatasetElement(String name, String type, String file) {
        this.name = name;
        this.type = type;
        this.file = file;
    }


    public String getPath() {
        return name + "/" + type + "/" + file;
    }

    public boolean isValid() {
        return getParts().length == 3;
    }

    public String[] getParts() {
        String[] parts = new String[3];
        parts[0] = name;
        parts[1] = type;
        parts[2] = file;
        return parts;
    }

    @Override
    public String toString() { return getPath(); }

    public String getType() {
        return type;
    }

    public String getFile() {
        return file;
    }

    public String getName() {
        return name;
    }
}
