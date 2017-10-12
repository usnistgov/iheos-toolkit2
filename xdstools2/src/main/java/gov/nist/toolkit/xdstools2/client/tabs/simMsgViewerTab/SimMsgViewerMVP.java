package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractMVP;
import gov.nist.toolkit.xdstools2.client.tabs.models.SimIdsModel;

/**
 *
 */
public class SimMsgViewerMVP extends AbstractMVP<SimIdsModel, SimMsgViewerView, SimMsgViewerPresenter>  {

    public SimMsgViewerMVP() {
        super();
        GWT.log("Build SimMsgViewerMVP");
    }
    @Override
    public SimMsgViewerView buildView() {
        return getView();
    }

    @Override
    public SimMsgViewerPresenter buildPresenter() {
        return getPresenter();
    }

}
