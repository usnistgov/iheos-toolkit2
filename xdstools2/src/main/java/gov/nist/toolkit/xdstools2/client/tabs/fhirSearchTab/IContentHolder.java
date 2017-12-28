package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.user.client.ui.Widget;

public interface IContentHolder {
    void addContent(Widget w, String title);
    void clearLogContent();
}
