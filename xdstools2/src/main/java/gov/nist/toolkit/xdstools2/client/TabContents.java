package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;

class TabContents {
    DockLayoutPanel panel;
    AbstractPresenter presenter;
    NotifyOnDelete notifyOnDelete = null;
    String tabName;

    TabContents(DockLayoutPanel panel,
            AbstractPresenter presenter,
                String tabName) {
        this.panel = panel;
        this.presenter = presenter;
        this.tabName = tabName;
    }

    public NotifyOnDelete getNotifyOnDelete() {
        return notifyOnDelete;
    }

    public void setNotifyOnDelete(NotifyOnDelete notifyOnDelete) {
        this.notifyOnDelete = notifyOnDelete;
    }


}
