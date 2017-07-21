package gov.nist.toolkit.datasets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class DatasetElement implements Serializable, IsSerializable {
    private String path;   // directory path starting from External_Cache/datasets/${datasetName}

    public DatasetElement(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }


}
