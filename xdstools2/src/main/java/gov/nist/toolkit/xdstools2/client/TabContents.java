package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;

class TabContents {
    DockLayoutPanel panel;
    AbstractPresenter presenter;
    NotifyOnDelete notifyOnDelete = null;

    TabContents(DockLayoutPanel panel,
            AbstractPresenter presenter) {
        this.panel = panel;
        this.presenter = presenter;
    }

    public NotifyOnDelete getNotifyOnDelete() {
        return notifyOnDelete;
    }

    public void setNotifyOnDelete(NotifyOnDelete notifyOnDelete) {
        this.notifyOnDelete = notifyOnDelete;
    }
}
