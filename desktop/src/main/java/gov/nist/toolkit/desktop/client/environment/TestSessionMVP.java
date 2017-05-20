package gov.nist.toolkit.desktop.client.environment;

import gov.nist.toolkit.desktop.client.abstracts.AbstractMVP;
import gov.nist.toolkit.desktop.client.models.NullModel;

import javax.inject.Inject;

/**
 *  Right now this seems useless
 */
public class TestSessionMVP extends AbstractMVP<NullModel, TestSessionView, TestSessionPresenter> {

    @Inject
    public TestSessionMVP(TestSessionView view, TestSessionPresenter presenter) {
        this.setPresenter(presenter);
        this.setView(view);
        init();
    }

    @Override
    public TestSessionView buildView() {
        return getView();
    }

    @Override
    public TestSessionPresenter buildPresenter() {
        return getPresenter();
    }
}
