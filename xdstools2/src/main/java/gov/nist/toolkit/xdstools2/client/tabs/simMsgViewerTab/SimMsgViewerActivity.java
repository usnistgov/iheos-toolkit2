package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.xdstools2.client.abstracts.ActivityDisplayer;
import gov.nist.toolkit.xdstools2.client.abstracts.GenericMVP;
import gov.nist.toolkit.xdstools2.client.command.command.GetFullSimId;
import gov.nist.toolkit.xdstools2.client.injector.Injector;
import gov.nist.toolkit.xdstools2.client.tabs.models.SimIdsModel;
import gov.nist.toolkit.xdstools2.client.util.ClientFactoryImpl;
import gov.nist.toolkit.xdstools2.shared.command.request.GetFullSimIdRequest;

/**
 *
 */
public class SimMsgViewerActivity extends AbstractToolkitActivity {

    private SimMsgViewerView view;
    private String simName = null;
    private String event = null;

    private SimMsgViewerPresenter presenter;

    private ActivityDisplayer displayer;

    public SimMsgViewerActivity(SimMsgViewer place) {
        super();
        GWT.log("Start activity ");
        if (place != null) {
            this.simName = place.getName();
            GWT.log("simName is " + simName);
            if (simName != null && simName.indexOf('/') != -1) {
                // really simname/actor/trans/event
                String[] parts = simName.split("/");
                if (parts.length == 4) {
                    simName = parts[0];
                    event = parts[3];
                    GWT.log("Event is " + event);
                }
            }
        }

        GWT.log("Build SimMsgViewerActivity for Place: " + simName);
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
    public void start(final AcceptsOneWidget acceptsOneWidget, final EventBus eventBus) {
        GWT.log("Starting SimMsgViewer Activity - simName is " + simName + " event is " + event);
        presenter = Injector.INSTANCE.getSimMsgViewerPresenter();
        view =      Injector.INSTANCE.getSimMsgViewerView();
        displayer = Injector.INSTANCE.getToolkitAppDisplayer();
        assert(presenter != null);

        presenter.setTitle("SimLog");
        if (simName != null && !simName.equals("")) {
            // need full id (all context) to proceed
            new GetFullSimId() {

                @Override
                public void onComplete(SimId simId) {
                    presenter.setCurrentSimId(simId);
                    presenter.setTitle("Log " + simId.toString());
                    finish(acceptsOneWidget, eventBus);
                }

            }.run(new GetFullSimIdRequest(Xdstools2.getHomeTab().getCommandContext(), new SimId(simName)));
        } else {
            finish(acceptsOneWidget, eventBus);
        }
    }

    private void finish(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        mvp = buildMVP();
        mvp.init();

        if (event != null) {
            presenter.preselectEvent(event);
        }

        presenter.setActivityDisplayer(displayer);  // so presenter can update tab title
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

    public void goTo(Place place) {
        new ClientFactoryImpl().getPlaceController().goTo(place);
    }

}
