package gov.nist.toolkit.datasets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DatasetModel implements Serializable, IsSerializable {
    private String name;
    private List<DatasetElement> items = new ArrayList<>();

    public DatasetModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<DatasetElement> getItems() {
        return items;
    }

    public DatasetModel add(DatasetElement ele) {
        items.add(ele);
        return this;
    }

}
