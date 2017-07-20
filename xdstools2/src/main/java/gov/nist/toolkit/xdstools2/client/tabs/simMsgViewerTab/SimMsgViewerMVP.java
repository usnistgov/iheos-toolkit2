package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import gov.nist.toolkit.xdstools2.client.abstracts.AbstractMVP;
import gov.nist.toolkit.xdstools2.client.tabs.models.SimIdsModel;

/**
 *
 */
public class SimMsgViewerMVP extends AbstractMVP<SimIdsModel, SimMsgViewerView, SimMsgViewerPresenter> {
    @Override
    public SimMsgViewerView buildView() {
        return getView();
    }

    @Override
    public SimMsgViewerPresenter buildPresenter() {
        return getPresenter();
    }}
