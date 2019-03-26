package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import gov.nist.toolkit.sitemanagement.client.Site;


public class UseTimestampProxyChangedHandler implements ValueChangeHandler {
    private ActorConfigTab actorConfigTab;
    Site site;
    CheckBox checkBox;

    UseTimestampProxyChangedHandler(ActorConfigTab actorConfigTab, Site site, CheckBox checkBox) {
        this.actorConfigTab = actorConfigTab;
        this.site = site;
        this.checkBox = checkBox;
    }



    @Override
    public void onValueChange(ValueChangeEvent valueChangeEvent) {
        site.useTimestampProxy = checkBox.getValue();
        actorConfigTab.currentEditSite.changed = true;
    }
}
