package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

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
public class SubmitResourceActivity extends AbstractToolkitActivity {

    private SubmitResourceView view;

    private SubmitResourcePresenter presenter;

    private ActivityDisplayer displayer;

    public SubmitResourceActivity() {
        super();
        GWT.log("Build SubmitResourceActivity");
    }

    private GenericMVP<DatasetModel, SubmitResourceView, SubmitResourcePresenter> mvp;

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
        GWT.log("Starting SubmitResource Activity");
        presenter = Injector.INSTANCE.getSubmitResourcePresenter();
        view =      Injector.INSTANCE.getSubmitResourceView();
        displayer = Injector.INSTANCE.getToolkitAppDisplayer();

        presenter.setTitle("SubmitResource");

        mvp = buildMVP();

        mvp.init();
        displayer.display(getContainer(), presenter.getTitle(), this, acceptsOneWidget, eventBus);
    }

    private GenericMVP<DatasetModel, SubmitResourceView, SubmitResourcePresenter> buildMVP() {
        return new GenericMVP<DatasetModel, SubmitResourceView, SubmitResourcePresenter>(view, presenter);
    }

    private Widget getContainer() {
        return mvp.getDisplay();
    }
}
