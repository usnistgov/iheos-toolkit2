package gov.nist.toolkit.desktop.client.legacy.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ActorConfigTab;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.SiteChoose;

/**
 *
 */
public class SiteEditAnchor extends Anchor {

    public SiteEditAnchor(String title, final SiteSpec siteSpec) {
        super(title);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ActorConfigTab actorConfigTab = new ActorConfigTab();
                actorConfigTab.onTabLoad(true, "");
                SiteChoose siteChoose = new SiteChoose(actorConfigTab);
                siteChoose.editSite(siteSpec);
            }
        });
    }
}
