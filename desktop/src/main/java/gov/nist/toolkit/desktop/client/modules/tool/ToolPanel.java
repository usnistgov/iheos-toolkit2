package gov.nist.toolkit.desktop.client.modules.tool;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.widget.ButtonStackPanel;
import gov.nist.toolkit.desktop.client.widget.ContextStackPanel;

/**
 *
 */
public class ToolPanel extends Composite implements ToolPresenter.MyView {
    interface ToolPanelUiBinder extends UiBinder<DockLayoutPanel, ToolPanel> {
    }

    private static ToolPanelUiBinder ourUiBinder = GWT.create(ToolPanelUiBinder.class);

    @UiField
    FlowPanel main;

    @UiField
    ContextStackPanel contextStack;

    @UiField
    ButtonStackPanel buttonStack;

    public ToolPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    void addMain(Widget widget) {
        main.add(widget);
    }

    void addContext(Widget widget) {
        contextStack.add(widget);
    }

    void addButton(String text) {
        buttonStack.addButton(text);
    }
}