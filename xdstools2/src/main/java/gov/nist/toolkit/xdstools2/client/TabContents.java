package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;

class TabContents {
    DockLayoutPanel panel;
    AbstractPresenter presenter;

    TabContents(DockLayoutPanel panel,
            AbstractPresenter presenter) {
        this.panel = panel;
        this.presenter = presenter;
    }
}
