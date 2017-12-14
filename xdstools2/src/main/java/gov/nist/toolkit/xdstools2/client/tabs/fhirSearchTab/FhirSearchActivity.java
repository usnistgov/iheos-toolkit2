package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.xdstools2.client.abstracts.ActivityDisplayer;
import gov.nist.toolkit.xdstools2.client.abstracts.GenericMVP;
import gov.nist.toolkit.xdstools2.client.injector.Injector;

/**
 *
 */
public class FhirSearchActivity extends AbstractToolkitActivity {

    private FhirSearchView view;

    private FhirSearchPresenter presenter;

    private ActivityDisplayer displayer;

    public FhirSearchActivity() {
        super();
        GWT.log("Build FhirSearchActivity");
    }

    private GenericMVP<DatasetModel, FhirSearchView, FhirSearchPresenter> mvp;

    @Override
    public GenericMVP getMVP() {
        return mvp;
    }

    @Override
    public LayoutPanel onResume() {
        return null;
    }

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting FhirSearch Activity");
        presenter = Injector.INSTANCE.getFhirSearchPresenter();
        view =      Injector.INSTANCE.getFhirSearchView();
        displayer = Injector.INSTANCE.getToolkitAppDisplayer();

        presenter.setTitle("FhirSearch");

        mvp = buildMVP();

        mvp.init();
        displayer.display(getContainer(), presenter.getTitle(), this, acceptsOneWidget, eventBus);
    }

    private GenericMVP<DatasetModel, FhirSearchView, FhirSearchPresenter> buildMVP() {
        return new GenericMVP<DatasetModel, FhirSearchView, FhirSearchPresenter>(view, presenter);
    }

    private Widget getContainer() {
        return mvp.getDisplay();
    }
}
