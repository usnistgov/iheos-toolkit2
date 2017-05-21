package gov.nist.toolkit.desktop.client.tools.getDocuments;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.ActivityDisplayer;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.desktop.client.abstracts.GenericMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

import javax.inject.Inject;

/**
 *
 */
public class GetDocumentsActivity extends AbstractToolkitActivity {

    @Inject
    private GetDocumentsView view;

    @Inject
    private GetDocumentsPresenter presenter;

    @Inject
    private ActivityDisplayer displayer;

    private GenericMVP<NullModel, GetDocumentsView,GetDocumentsPresenter> mvp;


    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting GetDocuments Activity");
        mvp = buildMVP();
        mvp.init();
        displayer.display(getContainer(), presenter.getTitle(), this, acceptsOneWidget, eventBus);
    }

    private Widget getContainer() {
        return mvp.getDisplay();
    }

    @Override
    public void onStop() {
        GWT.log("Stopping GetDocuments Activity ");
    }

    public void setName(String name) {
        presenter.name = name;
        presenter.setTitle(name);
    }

    private GenericMVP<NullModel, GetDocumentsView, GetDocumentsPresenter> buildMVP() {
        return new GenericMVP<NullModel, GetDocumentsView, GetDocumentsPresenter>(view, presenter);
    }

    public void setTitle(String title) {
        presenter.setTitle(title);
    }

    @Override
    public GenericMVP getMVP() {
        return mvp;
    }

    @Override
    public LayoutPanel onResume() {
        return null;
    }
}
