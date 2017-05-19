package gov.nist.toolkit.desktop.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 *
 */
public class ClosePanel extends Composite implements HasCloseHandlers<ClosePanel> {
    interface ClosePanelUiBinder extends UiBinder<HorizontalPanel, ClosePanel> {
    }

    private static ClosePanelUiBinder ourUiBinder = GWT.create(ClosePanelUiBinder.class);

    public ClosePanel() {
//        html = new HTML("Holder");
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiField(provided = false)
    HTML html;

    @UiHandler("close")
    void handleClick(ClickEvent event) {
        CloseEvent.fire(this, this);
    }

    public void setText(String text) {
//        html = new HTML(text);
        html.setText(text);
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ClosePanel> handler) {
        return addHandler(handler, CloseEvent.getType());
    }
}