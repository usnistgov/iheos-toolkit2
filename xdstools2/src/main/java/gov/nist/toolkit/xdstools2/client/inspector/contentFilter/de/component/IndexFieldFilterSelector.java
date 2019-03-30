package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValueCount;

import java.util.List;

public interface IndexFieldFilterSelector <S,T extends MetadataObject> extends IndexFieldValueCount, ValueChangeNotifier {
    Widget asWidget();
    S getFieldType();
    List<T> getResult();
    void addResult(List<T> result);
    void clearResult();
}
