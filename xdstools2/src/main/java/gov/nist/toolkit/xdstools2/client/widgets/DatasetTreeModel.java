package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DatasetTreeModel implements TreeViewModel {

    private static class Resource {
        private final String path;
        private final String name;

        Resource(String path, String name) {
            this.path = path;
            this.name = name;
            GWT.log("Build Resource " + path);
        }

        String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }

    private static class ResourceType {
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

    private static class Dataset {
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

    private List<Dataset> datasets = new ArrayList<>();

    public DatasetTreeModel() {}

    public void init(List<DatasetModel> content) {
        datasets.clear();

        for (DatasetModel model : content) {
            Dataset dataset = new Dataset(model.getName());
            datasets.add(dataset);

            for (DatasetElement ele : model.getItems()) {
                String type = ele.getType();
                String file = ele.getFile();

                ResourceType resourceType = dataset.getResourceType(type);
                resourceType.addResource(new Resource(getResourcePath(model.getName(), type, file), file));
            }
        }
    }

    private String getResourcePath(String datasetName, String resourceType, String file) {
        return datasetName + "/" + resourceType + "/" + file;
    }

    private final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();


    @Override
    public <T> NodeInfo<?> getNodeInfo(T t) {
        if (t == null) {
            // top level - datasets
            ListDataProvider<Dataset> dataProvider = new ListDataProvider<Dataset>(datasets);

            Cell<Dataset> cell = new AbstractCell<Dataset>() {
                @Override
                public void render(Context context, Dataset dataset, SafeHtmlBuilder safeHtmlBuilder) {
                    safeHtmlBuilder.appendEscaped(dataset.getName());
                }
            };
            return new DefaultNodeInfo<Dataset>(dataProvider, cell);
        }
        else if (t instanceof Dataset) {
            ListDataProvider<ResourceType> dataProvider = new ListDataProvider<ResourceType>(((Dataset)t).getTypes());

            Cell<ResourceType> cell = new AbstractCell<ResourceType>() {
                @Override
                public void render(Context context, ResourceType resourceType, SafeHtmlBuilder safeHtmlBuilder) {
                    if (resourceType != null)
                        safeHtmlBuilder.appendEscaped(resourceType.getName());
                }
            };
            return new DefaultNodeInfo<ResourceType>(dataProvider, cell);
        }
        else if (t instanceof ResourceType) {
            ListDataProvider<String> dataProvider
                    = new ListDataProvider<String>(
                    ((ResourceType) t).getResourceNames());

            // Use the shared selection model.
            return new DefaultNodeInfo<String>(dataProvider, new TextCell(),
                    selectionModel, null);
        }

        return null;
    }

    @Override
    public boolean isLeaf(Object o) {
        GWT.log("Type is " + o.getClass().getName());
        return o instanceof String;
    }

}
