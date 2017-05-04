package gov.nist.toolkit.desktop.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by bill on 5/3/17.
 */
public class ContextStackPanel extends Composite {
    interface ContextStackPanelUiBinder extends UiBinder<FlowPanel, ContextStackPanel> {
    }

    private static ContextStackPanelUiBinder ourUiBinder = GWT.create(ContextStackPanelUiBinder.class);

    @UiField
    FlowPanel panel;


    public ContextStackPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void add(Widget widget) {

        // Buttons to stack vertically
        widget.getElement().getStyle().setDisplay(Style.Display.BLOCK);

        panel.add(widget);
    }

}