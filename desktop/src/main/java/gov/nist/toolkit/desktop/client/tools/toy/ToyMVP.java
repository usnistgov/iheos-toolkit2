package gov.nist.toolkit.desktop.client.tools.toy;

import gov.nist.toolkit.desktop.client.abstracts.AbstractMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

/**
 *  Right now this seems useless
 */
public class ToyMVP extends AbstractMVP<NullModel, ToyView, ToyPresenter> {

    @Override
    public ToyView buildView() {
        return getView();
    }

    @Override
    public ToyPresenter buildPresenter() {
        return getPresenter();
    }
}
