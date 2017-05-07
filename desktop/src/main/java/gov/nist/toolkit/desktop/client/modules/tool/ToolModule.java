package gov.nist.toolkit.desktop.client.modules.tool;

import com.google.gwt.user.client.ui.Composite;

import javax.inject.Inject;

/**
 *
 */
public class ToolModule {
//    private static final TkGInjector INJECTOR = TkGInjector.INSTANCE;
//
//    private ToolPanel v = INJECTOR.getToolPanel();
//
//    private ToolPresenter p = INJECTOR.getToolPresenter();

    @Inject
    private ToolPanel v;

    @Inject
    private ToolPresenter p;

    @Inject
    public ToolModule() {
        assert(v != null);
        assert(p != null);
        p.init(v);
    }

    public Composite widget() {
        assert(v != null);
        return v;
    }

}
