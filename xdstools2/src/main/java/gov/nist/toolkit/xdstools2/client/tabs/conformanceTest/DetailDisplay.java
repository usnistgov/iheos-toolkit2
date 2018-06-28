package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class DetailDisplay extends FlowPanel {
    private HTML control;
    private ScrollPanel viewerPanel = new ScrollPanel();

    final private static String viewLabel  = "&boxplus;View Details";
    final private static String hideLabel = "&boxminus;Hide Details";

    private DetailDisplay() {
        viewerPanel.setVisible(false);
        control = new HTML(viewLabel);
        control.addStyleName("iconStyle");
        control.addStyleName("inlineLink");
        add(control);
        add(viewerPanel);
    }

    DetailDisplay(Widget widget) {
        this();
        control.addClickHandler(new DetailDisplayClickHandler(widget));
    }

    private class DetailDisplayClickHandler implements ClickHandler {
        private Widget contents;

        DetailDisplayClickHandler(Widget contents) {
            this.contents = contents;
        }


        @Override
        public void onClick(ClickEvent clickEvent) {
            viewerPanel.setVisible(!viewerPanel.isVisible());
            if (viewerPanel.isVisible()) {
                control.setHTML(hideLabel);
            } else {
                control.setHTML(viewLabel);
            }
            if (viewerPanel.getWidget() == null) {
                viewerPanel.add(contents);
            }
        }
    }
}
