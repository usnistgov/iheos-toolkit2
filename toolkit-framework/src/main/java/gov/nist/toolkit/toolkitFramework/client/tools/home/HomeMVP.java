package gov.nist.toolkit.toolkitFramework.client.tools.home;

import gov.nist.toolkit.toolkitFramework.client.models.NullModel;
import gov.nist.toolkit.toolkitFramework.client.mvp.AbstractMVP;

/**
 *
 */
public class HomeMVP extends AbstractMVP<NullModel, HomeView, HomePresenter> {
    @Override
    public HomeView buildView() {
        return new HomeView();
    }

    @Override
    public HomePresenter buildPresenter() {
        return new HomePresenter();
    }
}
