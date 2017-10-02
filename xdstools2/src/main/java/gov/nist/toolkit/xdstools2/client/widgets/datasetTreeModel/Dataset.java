package gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class Dataset {
    private final String name;
    private final List<ResourceType> types = new ArrayList<>();

    Dataset(String name) {
        this.name = name;
        GWT.log("Build Dataset " + name);
    }

    String getName() {
        return name;
    }

    void addType(ResourceType type) {
        types.add(type);
    }

    List<ResourceType> getTypes() {
        return types;
    }

    ResourceType getResourceType(String name) {
        for (ResourceType type : types) {
            if (type.getName().equals(name)) return type;
        }
        ResourceType type = new ResourceType(name);
        addType(type);
        return type;
    }
}
