package gov.nist.toolkit.xdstools2.client.injector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.abstracts.ToolkitAppDisplayer;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.InspectorPresenter;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.InspectorView;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.SubmitResourcePresenter;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.SubmitResourceView;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewerMVP;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewerPresenter;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewerView;

/**
 *
 */
@GinModules(InjectorModule.class)
public interface Injector extends Ginjector {
    Injector INSTANCE = GWT.create(Injector.class);

    Xdstools2EventBus getEventBus();


    SimMsgViewerPresenter getSimMsgViewerPresenter();
    SimMsgViewerView getSimMsgViewerView();
    SimMsgViewerMVP getSimMsgViewerMVP();

    TabContainer getTabContainer();
    ToolkitAppDisplayer getToolkitAppDisplayer();


    SubmitResourcePresenter getSubmitResourcePresenter();

    SubmitResourceView getSubmitResourceView();

    InspectorPresenter getInspectorPresenter();
    InspectorView getInspectorView();

}
