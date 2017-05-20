package gov.nist.toolkit.desktop.client.environment;

import gov.nist.toolkit.desktop.client.abstracts.AbstractMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

import javax.inject.Inject;

/**
 *  Right now this seems useless
 */
public class EnvironmentMVP extends AbstractMVP<NullModel, EnvironmentView, EnvironmentPresenter> {

    @Inject
    public EnvironmentMVP(EnvironmentView view, EnvironmentPresenter presenter) {
        this.setPresenter(presenter);
        this.setView(view);
        init();
    }

    @Override
    public EnvironmentView buildView() {
        return getView();
    }

    @Override
    public EnvironmentPresenter buildPresenter() {
        return getPresenter();
    }
}
