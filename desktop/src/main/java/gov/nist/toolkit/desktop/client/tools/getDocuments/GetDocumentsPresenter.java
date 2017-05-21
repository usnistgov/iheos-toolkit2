package gov.nist.toolkit.desktop.client.tools.getDocuments;

import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;

/**
 *
 */
public class GetDocumentsPresenter extends AbstractPresenter<GetDocumentsView> {

    String name = "";

    @Override
    public void init() {
        setTitle(name);
    }
}
