package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.util.HtmlUtil;

/**
 * DisclosurePanel contains header and body. Header contains title, time and icons.
 * Body contains description and sectionResultsPanel.
 */
public class TestSectionView implements IsWidget {
//    private final TestOverviewDisplayPanel header = new TestOverviewDisplayPanel();
    private final Header header = new Header();
    private final DisclosurePanel panel = new DisclosurePanel(header);
    private final FlowPanel body = new FlowPanel();
    private final FlowPanel sectionDescription = new FlowPanel();
    private final SectionResultsPanel sectionResultsPanel = new SectionResultsPanel();
    // Parts of the header
    private HTML description = new HTML();

    private HTML fatalError = new HTML();

    TestSectionView() {
    }

    void build() {
        header.build();

        body.clear();
        body.add(fatalError);
        body.add(description);

        body.add(sectionResultsPanel);

        panel.add(body);
    }

    class Header extends TestOverviewDisplayPanel implements TestStatusDisplay {
        HTML title = new HTML();
        Image statusIcon = null;
        HTML time = new HTML();
        Image play = null;
        Image done = null;

        void setSectionTitle(String text, String title) {
            this.title.setText(text);
            this.title.addStyleName("section-title");
            this.title.setTitle(title);
        }
        void setTime(String text) {
            time.setText(text);
        }
        void setPlay(String label, String title, ClickHandler clickHandler) {
            play = getImg("icons2/play-16.png", label, title);
            play.addClickHandler(clickHandler);
        }
        void setDone(String label, String title, ClickHandler clickHandler) {
            done = getImg("icons2/ic_forward_black_24dp_1x.png", title, label);
            if (clickHandler != null)
                done.addClickHandler(clickHandler);
        }
        @Override
        public void labelSuccess() {
            super.labelSuccess();
            statusIcon = new Image("icons2/correct-16.png");
            statusIcon.addStyleName("right");
            statusIcon.addStyleName("iconStyle");
        }

        @Override
        public void labelFailure() {
            super.labelFailure();
            statusIcon = new Image("icons/ic_warning_black_24dp_1x.png");
            statusIcon.addStyleName("right");
            statusIcon.addStyleName("iconStyle");
        }

        @Override
        public void labelNotRun() {
            super.labelNotRun();
        }
        void build() {
            clear();
            add(title);
            add(time);
            if (play != null) add(play);
            if (done != null) add(done);

            // flush right stuff
            if (statusIcon != null) add(statusIcon);
        }
    }

    // delegations to Header
    void setSectionTitle(String text, String title) { header.setSectionTitle(text, title);}
    void setTime(String text) { header.setTime(text); }
    void setPlay(String label, String title, ClickHandler clickHandler) { header.setPlay(label, title, clickHandler); }
    void setDone(String label, String title, ClickHandler clickHandler) { header.setDone(label, title, clickHandler); }
    void labelSuccess() { header.labelSuccess(); }
    void labelFailure() { header.labelFailure(); }
    void labelNotRun() { header.labelNotRun(); }

    private class SectionResultsPanel extends FlowPanel  {
        private FlowPanel testPlanDisplay = new FlowPanel();
        private FlowPanel stepPanel = new FlowPanel();

        SectionResultsPanel() {
            add(testPlanDisplay);
            add(stepPanel);
        }
    }

    // Delegations to SectionResultsPanel
    void setTestPlanDisplay(Widget display) {
        sectionResultsPanel.testPlanDisplay.clear();
        sectionResultsPanel.testPlanDisplay.add(display);
    }
    void addStepPanel(Widget panel) {
        sectionResultsPanel.stepPanel.add(panel);
    }

    void clearStepPanel() {
        sectionResultsPanel.stepPanel.clear();
    }

    private Image getImg(String iconFile, String tooltip, String altText) {
        Image imgIcon = new Image(iconFile);
        imgIcon.addStyleName("iconStyle");
        imgIcon.addStyleName("iconStyle_20x20");
        imgIcon.setTitle(tooltip);
        imgIcon.setAltText(altText);
        return imgIcon;
    }

    void setDescription(String description) {
        if (HtmlUtil.isHTML(description))
            this.description.setHTML(description);
        else
            this.description.setText(description);
    }

    void setFatalError(String errorMsg) {
        fatalError.setText("Fatal Error: " + errorMsg + "<br />");
    }

    void addOpenHandler(OpenHandler<DisclosurePanel> handler) {
        panel.addOpenHandler(handler);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
