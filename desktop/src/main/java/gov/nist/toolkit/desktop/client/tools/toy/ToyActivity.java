package gov.nist.toolkit.desktop.client.tools.toy;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.ActivityDisplayer;
import gov.nist.toolkit.desktop.client.abstracts.GenericMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

import javax.inject.Inject;

/**
 *
 */
public class ToyActivity extends AbstractActivity {

    @Inject
    private ToyView view;

    @Inject
    private ToyPressnter presenter;

    @Inject
    private ActivityDisplayer displayer;

    private GenericMVP<NullModel,ToyView,ToyPressnter> mvp;


    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting Toy Activity");
        mvp = buildMVP();
        mvp.init();
        displayer.display(getContainer(), this, acceptsOneWidget, eventBus);
    }

    private Widget getContainer() {
        return mvp.getDisplay();
    }

    @Override
    public void onStop() {
        GWT.log("Stopping Toy Activity " + presenter.myIndex);
    }

    public void setName(String name) {
        presenter.name = name;
    }

    private GenericMVP<NullModel, ToyView, ToyPressnter> buildMVP() {
        return new GenericMVP<NullModel, ToyView, ToyPressnter>(view, presenter);
    }

}
