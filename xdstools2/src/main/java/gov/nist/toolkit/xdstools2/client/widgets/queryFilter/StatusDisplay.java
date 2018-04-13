package gov.nist.toolkit.xdstools2.client.widgets.queryFilter;

import com.google.gwt.user.client.ui.VerticalPanel;

public interface StatusDisplay {
    VerticalPanel getResultPanel();
    void setStatus(String message, boolean status);
}
