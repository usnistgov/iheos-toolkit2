package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.util.HtmlUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * View for TestDisplay
 */
class TestDisplayView extends FlowPanel implements TestStatusDisplay {
    private TestOverviewDisplayPanel header = new TestOverviewDisplayPanel();
    private FlowPanel body = new FlowPanel();
    private DisclosurePanel panel = new DisclosurePanel(header);

    // Parts of the header
    private HTML title = new HTML();
    private HTML time = new HTML();
    private Image play = null;
    private Image delete = null;
    private Image inspect = null;
    private Image statusIcon = null;

    // Parts of the body
    private HTML description = new HTML();
    private Widget interactionDiagram = null;
    private List<Widget> sections = new ArrayList<>();

    TestDisplayView() {
        header.fullWidth();
        panel.setWidth("100%");
        panel.add(body);
        add(panel);
    }

    /**
     * Call after all the setting is done
     */
    void display() {
        header.clear();
        header.add(title);
        header.add(time);
        if (play != null) header.add(play);

        // flush right stuff
        if (delete != null) header.add(delete);
        if (statusIcon != null) header.add(statusIcon);
        if (inspect != null) header.add(inspect);

        body.clear();
        body.add(description);
        if (interactionDiagram != null) body.add(interactionDiagram);
        for (Widget section : sections) {
            body.add(section);
        }
    }

    void setTestTitle(String text) {
        title.setText(text);
        title.addStyleName("test-title");
    }

    void setTime(String text) {
        time.setText(text);
    }

    void setPlay(String title, ClickHandler clickHandler) {
        play = new Image("icons2/play-24.png");
        play.setStyleName("iconStyle");
        play.setTitle(title);
        play.addClickHandler(clickHandler);
    }

    void setDelete(String title, ClickHandler clickHandler) {
        delete = new Image("icons2/garbage-24.png");
        delete.addStyleName("right");
        delete.addStyleName("iconStyle");
        delete.addClickHandler(clickHandler);
        delete.setTitle(title);
    }

    void setInspect(String title, ClickHandler clickHandler) {
        inspect = new Image("icons2/visible-32.png");
        inspect.addStyleName("right");
        inspect.addClickHandler(clickHandler);
        inspect.setTitle(title);
    }

    void setDescription(String description) {
        if (HtmlUtil.isHTML(description))
            this.description.setHTML(description);
        else
            this.description.setText(description);
    }

    void setInteractionDiagram(Widget diagram) { interactionDiagram = diagram; }

    void clearSections() { sections.clear(); }
    void addSection(Widget section) { sections.add(section); }

    @Override
    public void labelSuccess() {
        header.labelSuccess();
        statusIcon = new Image("icons2/correct-24.png");
        statusIcon.addStyleName("right");
        statusIcon.addStyleName("iconStyle");
    }

    @Override
    public void labelFailure() {
        header.labelFailure();
        statusIcon = new Image("icons/ic_warning_black_24dp_1x.png");
        statusIcon.addStyleName("right");
        statusIcon.addStyleName("iconStyle");
    }

    @Override
    public void labelNotRun() {
        header.labelNotRun();
    }
}
