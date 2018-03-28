package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class TestSessionChoose implements ChangeHandler {
    private ActorConfigTab actorConfigTab;
    private ListBox listBox;

    public TestSessionChoose(ActorConfigTab tab, ListBox listBox) {
        this.actorConfigTab = tab;
        this.listBox = listBox;
    }

    @Override
    public void onChange(ChangeEvent changeEvent) {
        String value = listBox.getSelectedValue();
        actorConfigTab.currentEditSite.setOwner(value);
        actorConfigTab.currentEditSite.changed = true;
    }
}
