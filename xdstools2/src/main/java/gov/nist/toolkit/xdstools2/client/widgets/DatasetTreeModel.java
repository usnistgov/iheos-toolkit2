package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
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
        private final String name;

        Resource(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    private static class ResourceType {
        private final String name;
        private final List<Resource> resources = new ArrayList<>();

        ResourceType(String name) {
            this.name = name;
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
    }

    private static class Dataset {
        private final String name;
        private final List<ResourceType> types = new ArrayList<>();

        Dataset(String name) {
            this.name = name;
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

    public void init(Map<String, DatasetModel> content) {

        for (String name : content.keySet()) {
            DatasetModel model = content.get(name);

            Dataset dataset = new Dataset(name);
            datasets.add(dataset);

            for (DatasetElement ele : model.getItems()) {
                String type = ele.getType();
                String file = ele.getFile();

                ResourceType resourceType = dataset.getResourceType(type);
                resourceType.addResource(new Resource(file));
            }
        }
    }


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
                    safeHtmlBuilder.appendEscaped(resourceType.getName());
                }
            };
        }
        else if (t instanceof ResourceType) {
            ListDataProvider<Resource> dataProvider = new ListDataProvider<Resource>(((ResourceType)t).getResources());

            Cell<Resource> cell = new AbstractCell<Resource>() {
                @Override
                public void render(Context context, Resource resource, SafeHtmlBuilder safeHtmlBuilder) {
                    safeHtmlBuilder.appendEscaped(resource.getName());                }
            };
        }

        return null;
    }

    @Override
    public boolean isLeaf(Object o) {
        return o instanceof ResourceType;
    }

}
