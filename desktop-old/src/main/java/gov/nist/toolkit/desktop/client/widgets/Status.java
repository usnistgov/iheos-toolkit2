package gov.nist.toolkit.desktop.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;

/**
 *
 */
public class Status extends Composite implements HasText {
    interface StatusUiBinder extends UiBinder<HTMLPanel, Status> {
    }

    private static StatusUiBinder ourUiBinder = GWT.create(StatusUiBinder.class);

    @UiField
    HTML html;

    public Status() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setText(String text) {
        html.setText(text);
    }

    public String getText() {
        return html.getText();
    }
}