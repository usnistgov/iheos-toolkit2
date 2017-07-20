package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;

import javax.inject.Inject;

/**
 *
 */
public class SimMsgViewerPresenter extends AbstractPresenter<SimMsgViewerView> {

    @Inject
    public SimMsgViewerPresenter() {
        super();
        GWT.log("Build SimMsgViewerPresenter");
    }

    @Override
    public void init() {

    }
}
