package gov.nist.toolkit.desktop.client.tools.AbstractTool;

import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

/**
 *
 */
public class ToolPresenter {
    public interface MyView {
    }

    private EventBus eventBus;

    ToolPanel v;

    @Inject
    public ToolPresenter(EventBus eventBus) {
        this.eventBus = eventBus;
        assert(eventBus != null);
    }

    protected void init(ToolPanel v) {
        this.v = v;

        v.addMain(new Label("main"));
        v.addContext(new Label("Environment"));
        v.addButton("Reset");

        bindUI();
    }

    private void bindUI() {

    }

}
