package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractMVP;

/**
 *
 */
public class SubmitResourceMVP extends AbstractMVP<DatasetModel, SubmitResourceView, SubmitResourcePresenter> {

    public SubmitResourceMVP() {
        super();
        GWT.log("Build SubmitResourceMVP");
    }

    @Override
    public SubmitResourceView buildView() {
        return getView();
    }

    @Override
    public SubmitResourcePresenter buildPresenter() {
        return getPresenter();
    }
}
