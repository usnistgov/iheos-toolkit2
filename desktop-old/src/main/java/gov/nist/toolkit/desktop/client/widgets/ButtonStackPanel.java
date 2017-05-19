package gov.nist.toolkit.desktop.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 *
 */
public class ButtonStackPanel extends Composite {
    interface ButtonStackPanelUiBinder extends UiBinder<FlowPanel, ButtonStackPanel> {
    }

    private static ButtonStackPanelUiBinder ourUiBinder = GWT.create(ButtonStackPanelUiBinder.class);

    @UiField
    FlowPanel panel;

    public ButtonStackPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addButton(String text) {
        Button button = new Button(text);

        // Buttons to stack vertically
        button.getElement().getStyle().setDisplay(Style.Display.BLOCK);

        panel.add(button);
    }
}