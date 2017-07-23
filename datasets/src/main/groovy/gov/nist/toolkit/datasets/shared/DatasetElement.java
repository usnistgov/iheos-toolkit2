package gov.nist.toolkit.datasets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class DatasetElement implements Serializable, IsSerializable {
    private String type;
    private String file;

    public DatasetElement(String type, String file) {
        this.type = type;
        this.file = file;
    }

    /**
     *
     * @param path   type/file.ext format
     */
    public DatasetElement(String path) {
        this(part(path, 0), part(path, 1));
    }

    public DatasetElement() {
    }

    private static String part(String data, int part) {
        String[] parts = data.split("/");
        if (parts.length <= part) return null;
        return parts[part];
    }

    public String getType() {
        return type;
    }

    public String getFile() {
        return file;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
