package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValueCount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class IndexFieldFilterSelector <S,T extends MetadataObject> extends Widget implements IndexFieldValueCount, ValueChangeNotifier {
    public abstract Widget asWidget();
    public abstract S getFieldType();

    /**
     * Aggregate result. Not tied to any specific option/value.
     * @return
     */
    public abstract List<T> getResult();
    public abstract void addResult(List<T> result);
    public abstract void clearResult();

    public boolean isDeferredIndex() {
        return false;
    }

    /**
     * Filter based on what values/data are available and what is of interest through the selected values.
     * Also set the counter fields.
     * @param fieldValueMap
     * @return
     */
    public List<T> filter(Map<IndexFieldValue, List<T>> fieldValueMap) {
        List<T> result = new ArrayList<>();

        if (fieldValueMap != null) {
            for (IndexFieldValue ifv : fieldValueMap.keySet()) {
                List<T> list = fieldValueMap.get(ifv);
                if (list != null && ! list.isEmpty()) {
                    doUpdateCount(ifv, list.size());
                    Set<IndexFieldValue> selectedValues = getSelectedValues();
                    if (selectedValues != null) {
                        if (selectedValues.isEmpty()) {
                            result.addAll(list);
                        } else if (selectedValues.contains(ifv)) {
                            result.addAll(list);
                        }
                    }
                } else {
                   doUpdateCount(ifv, 0);
                }
            }
        }
        return result;
    }


    /**
     * Deferred index filter. Intended to be overridden by the selector.
     * @param inputList
     * @return
     */
    public List<T> filter(List<T> inputList) {
       return null;
    }
}
