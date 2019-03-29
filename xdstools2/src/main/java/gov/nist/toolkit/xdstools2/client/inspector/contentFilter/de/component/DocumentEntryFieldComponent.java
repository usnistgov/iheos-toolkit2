package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValueCount;

public interface DocumentEntryFieldComponent extends IndexFieldValueCount, ValueChangeNotifier {
    Widget asWidget();
}
