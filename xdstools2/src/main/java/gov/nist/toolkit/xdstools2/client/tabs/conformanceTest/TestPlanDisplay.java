package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.nist.toolkit.xdstools2.client.sh.BrushFactory;
import gov.nist.toolkit.xdstools2.client.sh.SyntaxHighlighter;

/**
 *
 */
public class TestPlanDisplay extends FlowPanel {
    private HTML testplanCtl;
    private ScrollPanel testplanViewerPanel = new ScrollPanel();

    final private static String viewTestplanLabel  = "&boxplus;View Testplan";
    final private static String hideTestplanLabel = "&boxminus;Hide Testplan";

    private TestPlanDisplay() {
        testplanViewerPanel.setVisible(false);
        testplanCtl = new HTML(viewTestplanLabel);
        testplanCtl.addStyleName("iconStyle");
        testplanCtl.addStyleName("inlineLink");
        add(testplanCtl);
        add(testplanViewerPanel);
    }

    TestPlanDisplay(String htmlizedTestPlan) {
        this();
        testplanCtl.addClickHandler(new ViewTestplanClickHandler(htmlizedTestPlan));
    }

    private class ViewTestplanClickHandler implements ClickHandler {
        private String htmlizedStr;

        ViewTestplanClickHandler(String secTestplanStr) {
            this.htmlizedStr = secTestplanStr;
        }

        @Override
        public void onClick(ClickEvent clickEvent) {
            testplanViewerPanel.setVisible(!testplanViewerPanel.isVisible());
            if (!testplanViewerPanel.isVisible()) {
                testplanCtl.setHTML(viewTestplanLabel);
            } else {
                testplanCtl.setHTML(hideTestplanLabel);
            }
            if (testplanViewerPanel.getWidget()==null) {
                testplanViewerPanel.add(getShHtml(htmlizedStr));
            }
        }
    }

    static HTML getShHtml(String xmlStr) {
        return new HTML(SyntaxHighlighter.highlight(xmlStr, BrushFactory.newXmlBrush(), false));
    }

}
