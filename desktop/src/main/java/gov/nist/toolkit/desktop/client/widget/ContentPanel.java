package gov.nist.toolkit.desktop.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class ContentPanel extends Composite {
    interface ContentPanelUiBinder extends UiBinder<Widget, ContentPanel> {
    }

    private static ContentPanelUiBinder ourUiBinder = GWT.create(ContentPanelUiBinder.class);

    @UiField
    TabLayoutPanel tab;

    public ContentPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addTab(String text, Composite content) {
        tab.add(content,text);
        tab.selectTab(tab.getWidgetCount() - 1);
    }
}