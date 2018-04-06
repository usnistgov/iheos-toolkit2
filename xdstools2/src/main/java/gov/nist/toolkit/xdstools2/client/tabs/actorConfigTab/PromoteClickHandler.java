package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.PromoteCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.PromoteRequest;

public class PromoteClickHandler implements ClickHandler {
    ActorConfigTab tab;

    PromoteClickHandler(ActorConfigTab tab) {
        this.tab = tab;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (tab.currentEditSite == null)
            return;

        if (!new SaveButtonClickHandler(tab).save())
            return;

        if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
            new PopupMessage("You don't have permission to create a promote a System in this Test Session");
            return;
        }

        new PromoteCommand(){

            @Override
            public void onComplete(String result) {
                ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
                new PopupMessage("Success!");
            }
        }.run(new PromoteRequest(ClientUtils.INSTANCE.getCommandContext(), new SiteSpec(tab.currentEditSite.getName(), tab.currentEditSite.getTestSession())));
    }

}

