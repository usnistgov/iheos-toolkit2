package gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class ResourceType {
    private final String name;
    private final List<Resource> resources = new ArrayList<>();

    ResourceType(String name) {
        this.name = name;
        GWT.log("Build ResourceType " + name);
    }

    String getName() {
        return name;
    }

    void addResource(Resource resource) {
        resources.add(resource);
    }

    List<Resource> getResources() {
        return resources;
    }

    List<String> getResourceNames() {
        List<String> names = new ArrayList<String>();
        for (Resource r : getResources()) {
            names.add(r.getName());
        }
        return names;
    }

    List<String> getResourcePaths() {
        List<String> paths = new ArrayList<String>();
        for (Resource r : getResources()) {
            paths.add(r.getPath());
        }
        return paths;
    }
}
