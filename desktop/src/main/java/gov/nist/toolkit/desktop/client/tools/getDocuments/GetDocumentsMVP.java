package gov.nist.toolkit.desktop.client.tools.getDocuments;

import gov.nist.toolkit.desktop.client.abstracts.AbstractMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

/**
 *  Right now this seems useless
 */
public class GetDocumentsMVP extends AbstractMVP<NullModel, GetDocumentsView, GetDocumentsPresenter> {

    @Override
    public GetDocumentsView buildView() {
        return getView();
    }

    @Override
    public GetDocumentsPresenter buildPresenter() {
        return getPresenter();
    }
}
