package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.xdstools2.client.abstracts.ActivityDisplayer;
import gov.nist.toolkit.xdstools2.client.abstracts.GenericMVP;
import gov.nist.toolkit.xdstools2.client.injector.Injector;
import gov.nist.toolkit.xdstools2.client.tabs.models.SimIdsModel;

/**
 *
 */
public class SimMsgViewerActivity extends AbstractToolkitActivity {

    private SimMsgViewerView view;


    private SimMsgViewerPresenter presenter;

    private ActivityDisplayer displayer;

    public SimMsgViewerActivity() {
        super();
        GWT.log("Build SimMsgViewerActivity");
    }

    private GenericMVP<SimIdsModel,SimMsgViewerView,SimMsgViewerPresenter> mvp;

    @Override
    public GenericMVP getMVP() {
        assert(mvp != null);
        return mvp;
    }

    @Override
    public LayoutPanel onResume() {
        return null;
    }

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting SimMsgViewer Activity");
        presenter = Injector.INSTANCE.getSimMsgViewerPresenter();
        view =      Injector.INSTANCE.getSimMsgViewerView();
        displayer = Injector.INSTANCE.getToolkitAppDisplayer();
        assert(presenter != null);
        mvp = buildMVP();
        assert(mvp != null);
        mvp.init();
        displayer.display(getContainer(), presenter.getTitle(), this, acceptsOneWidget, eventBus);
    }

    private GenericMVP<SimIdsModel, SimMsgViewerView, SimMsgViewerPresenter> buildMVP() {
        assert(presenter != null);
        assert(view != null);
        return new GenericMVP<SimIdsModel, SimMsgViewerView, SimMsgViewerPresenter>(view, presenter);
    }

    private Widget getContainer() {
        assert(mvp != null);
        return mvp.getDisplay();
    }
}
