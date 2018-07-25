package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.util.HtmlUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * View for TestDisplay
 */
public class TestDisplayView extends FlowPanel implements TestStatusDisplay {
    private TestOverviewDisplayPanel header = new TestOverviewDisplayPanel();
    private FlowPanel body = new FlowPanel();
    private DisclosurePanel panel = new DisclosurePanel(header);

    // Parts of the header
    private HTML title = new HTML();
    private HTML time = new HTML();
    private Image play = null;
    private Image delete = null;
    private Image validate = null;
    private Image inspect = null;
    private Image statusIcon = null;
    private Image testKitSourceIcon = null;
    private HTML tls = new HTML();

    // Parts of the body
    private HTML description = new HTML();
    private Widget interactionDiagram = null;
    private List<TestSectionDisplay> sections = new ArrayList<>();

    private HandlerRegistration openSectionIfOnlyOneHReg = null;
    private HandlerRegistration openTestBarHReg = null;

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
        if (validate != null) header.add(validate);

        // flush right stuff
        if (testKitSourceIcon!=null) header.add(testKitSourceIcon);
        header.add(tls);
        if (delete != null) header.add(delete);
        if (statusIcon != null) header.add(statusIcon);
        if (inspect != null) header.add(inspect);

        body.clear();
        body.add(description);
        if (interactionDiagram != null) body.add(interactionDiagram);
        for (TestSectionDisplay section : sections) {
            body.add(section.asWidget());
        }
        if (openSectionIfOnlyOneHReg == null) { // This is a one-time handler registration
            openSectionIfOnlyOneHReg = addOpenHandler(new OpenHandler<DisclosurePanel>() {
                @Override
                public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
                    autoOpenIfOnlyOneSection();
                }
            });
        }
    }

    public void autoOpenIfOnlyOneSection() {
        if (sections.size() == 1)
            sections.get(0).open();
    }

    public HandlerRegistration addOpenHandler(OpenHandler<DisclosurePanel> openHandler) {
       return panel.addOpenHandler(openHandler);
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

    void setValidate(String title, ClickHandler clickHandler) {
        validate = new Image("icons2/validate-32.png");
        validate.addStyleName("iconStyle");
        validate.addClickHandler(clickHandler);
        validate.setTitle(title);
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

    public Widget getInteractionDiagram() {
        return interactionDiagram;
    }

    void setInteractionDiagram(Widget diagram) {
        interactionDiagram = diagram;
    }

    void clearSections() { sections.clear(); }
    void addSection(TestSectionDisplay section) { sections.add(section); }

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

    @Override
    public void addExtraStyle(String name) {
       header.addExtraStyle(name);
    }

    @Override
    public void removeExtraStyle(String name) {
        header.removeExtraStyle(name);
    }

    public void labelTls() {
        tls.setHTML("TLS");
    }

    void setTestKitSourceIcon(String testKitSource, String testKitSection) {
        String sourceIcon = null;
        if ("Embedded".equals(testKitSource)) {
            // Too verbose
            // sourceIcon = "icons2/capital_e-24.png";
           return;
        } else if ("Local".equals(testKitSource)) {
            sourceIcon = "icons2/capital_l-24.png";
        } else {
            sourceIcon = "icons2/red_questionmark-24.png";
        }
        testKitSourceIcon = new Image(sourceIcon);
        String displayMsg = "TestKit Source: " + testKitSource + "; Section: " + testKitSection;
        testKitSourceIcon.setTitle(displayMsg);
        testKitSourceIcon.setAltText(displayMsg);

        testKitSourceIcon.addStyleName("right");
        testKitSourceIcon.addStyleName("iconStyle");
        testKitSourceIcon.addStyleName("iconStyle_20x20");
    }

    public HandlerRegistration getOpenSectionIfOnlyOneHReg() {
        return openSectionIfOnlyOneHReg;
    }

    public void setOpenSectionIfOnlyOneHReg(HandlerRegistration openSectionIfOnlyOneHReg) {
        this.openSectionIfOnlyOneHReg = openSectionIfOnlyOneHReg;
    }

    public HandlerRegistration getOpenTestBarHReg() {
        return openTestBarHReg;
    }

    public void setOpenTestBarHReg(HandlerRegistration openTestBarHReg) {
        this.openTestBarHReg = openTestBarHReg;
    }
}
