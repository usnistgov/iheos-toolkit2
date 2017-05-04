package gov.nist.toolkit.desktop.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class ToolPanel extends Composite {
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

    public void addMain(Widget widget) {
        main.add(widget);
    }

    public void addContext(Widget widget) {
        contextStack.add(widget);
    }

    public void addButton(String text) {
        buttonStack.addButton(text);
    }
}