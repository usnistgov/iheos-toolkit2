package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

public class MessageDisplayView implements IsWidget {
    String title;
    HorizontalFlowPanel outerPanel = new HorizontalFlowPanel();
    ScrollPanel scrollPanel = new ScrollPanel();
    FlowPanel menuPanel = new FlowPanel();
    ScrollPanel menuScrollPanel = new ScrollPanel();
    FlowPanel contentPanel = new FlowPanel();

    public MessageDisplayView(String title) {
        this.title = title;
        outerPanel.add(menuScrollPanel);
        menuScrollPanel.add(menuPanel);
        outerPanel.add(scrollPanel);
        scrollPanel.add(contentPanel);

        menuScrollPanel.setWidth("27%");
        menuScrollPanel.setHeight("100%");
        menuPanel.setWidth("100%");
        menuPanel.setHeight("100%");
        outerPanel.setWidth("100%");
        outerPanel.setHeight("100%");
        scrollPanel.setWidth("70%");
        scrollPanel.setHeight("100%");
        contentPanel.setWidth("100%");
        contentPanel.setHeight("100%");

        menuPanel.addStyleName("with-border");
        menuPanel.addStyleName("no-margin");

    }

    MessageDisplayView clear() {
        contentPanel.clear();
        return this;
    }

    FlowPanel newContent() {
        contentPanel.clear();
        return contentPanel;
    }

    FlowPanel newMenu() {
        menuPanel.clear();
        return menuPanel;
    }

    FlowPanel getContentPanel() {
        return contentPanel;
    }

    @Override
    public Widget asWidget() {
        return outerPanel;
    }
}
