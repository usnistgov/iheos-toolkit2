package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import elemental.events.Event;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class DatasetTreeModel implements TreeViewModel {

    private static class Resource {
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
            return new DatasetElement(name, path);
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
    DatasetTreeModel myModel;

    public DatasetTreeModel() {
        this.myModel = this;
    }

    public void init(List<DatasetModel> content) {
        datasets.clear();

        for (DatasetModel model : content) {
            Dataset dataset = new Dataset(model.getName());
            datasets.add(dataset);

            for (DatasetElement ele : model.getItems()) {
                String type = ele.getType();
                String file = ele.getFile();

                ResourceType resourceType = dataset.getResourceType(type);
                resourceType.addResource(new Resource(getResourcePath(model.getName(), type, file), type, file));
            }
        }
    }

    private String getResourcePath(String datasetName, String resourceType, String file) {
        return datasetName + "/" + resourceType + "/" + file;
    }

    private final SingleSelectionModel<Resource> selectionModel = new SingleSelectionModel<Resource>();


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
            ListDataProvider<Resource> dataProvider
                    = new ListDataProvider<Resource>(
                    ((ResourceType) t).getResources());

            // Use the shared selection model.
            return new DefaultNodeInfo<Resource>(dataProvider, new MyCell(this),
                    selectionModel, null);
        }

        return null;
    }

    @Override
    public boolean isLeaf(Object o) {
        GWT.log("Type is " + o.getClass().getName());
        return o instanceof Resource;
    }

    private static class MyCell extends AbstractCell<Resource> {
        DatasetTreeModel model;

        MyCell(DatasetTreeModel model) {
            super(Event.CLICK);
            this.model = model;
        }

        @Override
        public void render(Context context, Resource resource, SafeHtmlBuilder safeHtmlBuilder) {
            if (resource == null) return;
            safeHtmlBuilder.appendEscaped(resource.getName());
        }

        @Override
        public void onBrowserEvent(Context context, com.google.gwt.dom.client.Element parent, Resource value, NativeEvent event, ValueUpdater<Resource> valueUpdater) {
            String eventType = event.getType();
            if("click".equals(eventType)) {
                this.onClick(context, parent, value, event, valueUpdater);
            }

        }
        void onClick(Context context, com.google.gwt.dom.client.Element parent, Resource resource, NativeEvent event, ValueUpdater<Resource> valueUpdater) {
            model.doSelect(resource.getDatasetElement());
        }
    }

    public abstract void doSelect(DatasetElement datasetElement);

}
