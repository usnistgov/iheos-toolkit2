package gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class DatasetTreeModel implements TreeViewModel {

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

    public abstract void doSelect(DatasetElement datasetElement);

}
