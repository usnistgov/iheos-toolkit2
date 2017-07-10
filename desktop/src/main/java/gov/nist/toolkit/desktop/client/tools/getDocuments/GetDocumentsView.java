package gov.nist.toolkit.desktop.client.tools.getDocuments;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.abstracts.AbstractView;

import javax.inject.Inject;
import java.util.Map;

/**
 *
 */
public class GetDocumentsView extends AbstractView<GetDocumentsPresenter> {
    private TextArea textArea;

    @Inject
    private GetDocumentsTab getDocumentsTab;

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        assert (getDocumentsTab != null);

        return getDocumentsTab.buildUI();
    }

    @Override
    protected void bindUI() {

    }
}
