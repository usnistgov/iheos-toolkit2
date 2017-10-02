package gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.datasets.shared.DatasetElement;

/**
 *
 */
class Resource {
    private final String path;
    private final String name;
    private final String type;

    Resource(String path, String type, String name) {
        this.path = path;
        this.name = name;
        this.type = type;
        GWT.log("Build Resource " + path);
    }

    String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public DatasetElement getDatasetElement() {
        return new DatasetElement(name, type, path);
    }
}
