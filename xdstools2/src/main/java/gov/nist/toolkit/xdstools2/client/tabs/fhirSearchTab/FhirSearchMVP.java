package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractMVP;

/**
 *
 */
public class FhirSearchMVP extends AbstractMVP<DatasetModel, FhirSearchView, FhirSearchPresenter> {

    public FhirSearchMVP() {
        super();
        GWT.log("Build FhirSearchMVP");
    }

    @Override
    public FhirSearchView buildView() {
        return getView();
    }

    @Override
    public FhirSearchPresenter buildPresenter() {
        return getPresenter();
    }
}
