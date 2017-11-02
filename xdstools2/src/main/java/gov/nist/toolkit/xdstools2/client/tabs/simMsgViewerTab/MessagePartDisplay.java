package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

class MessagePartDisplay implements ClickHandler {
    private HTML content;
    private FlowPanel panel;

    public MessagePartDisplay(FlowPanel panel, HTML content) {
        this.panel = panel;
        this.content = content;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        panel.clear();
        panel.add(content);
    }
}
