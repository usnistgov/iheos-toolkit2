package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import gov.nist.toolkit.sitemanagement.client.Site;


public class UseFilterProxyChangedHandler implements ValueChangeHandler {
    private ActorConfigTab actorConfigTab;
    Site site;
    CheckBox checkBox;

    UseFilterProxyChangedHandler(ActorConfigTab actorConfigTab, Site site, CheckBox checkBox) {
        this.actorConfigTab = actorConfigTab;
        this.site = site;
        this.checkBox = checkBox;
    }



    @Override
    public void onValueChange(ValueChangeEvent valueChangeEvent) {
        site.useFilterProxy = checkBox.getValue();
        actorConfigTab.currentEditSite.changed = true;
    }
}
