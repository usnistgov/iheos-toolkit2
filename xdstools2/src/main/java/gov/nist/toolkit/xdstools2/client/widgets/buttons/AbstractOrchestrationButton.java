package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.MessageItem;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.ErrorHandler;

/**
 *
 */
abstract public class AbstractOrchestrationButton implements ClickHandler {
    private final FlowPanel panel = new FlowPanel();  // upper panel
    private final FlowPanel errorPanel = new FlowPanel();
    private Panel topPanel;
    private CheckBox resetCheckBox = null;
    private Panel customPanel = null;
    private boolean errorPanelAdded = false;
    private CheckBox selftestCheckBox;
    private String label = null;
    private String resetLabel = null;

    public AbstractOrchestrationButton(Panel topPanel, String label) {
        this.topPanel = topPanel;
        build();
    }

    protected AbstractOrchestrationButton() {}

    protected void setParentPanel(Panel parent) {
        this.topPanel = parent;
    }

    protected void setCustomPanel(Panel panel) {
        this.customPanel = panel;
    }

    protected void setLabel(String label) {
        this.label = label;
    }

    protected void setResetLabel(String resetLabel) {
        this.resetLabel = resetLabel;
    }

    public Panel build() {
        if (!errorPanelAdded) {
            errorPanelAdded = true;
            panel.add(errorPanel);
        }
        panel.add(new HTML("<hr /><h2>Testing Environment</h2>"));

        if (customPanel != null) {
            panel.add(customPanel);
        }

        String resetLabel = "Reset - Initialize will delete all supporting simulators and Patient IDs and recreate them.";
        resetCheckBox = new CheckBox(resetLabel);
        panel.add(resetCheckBox);
        panel.add(new HTML("<br />"));

        selftestCheckBox = new CheckBox("Self Test - Enable self test mode.");
        panel.add(selftestCheckBox);
        panel.add(new HTML("<br />"));

        final Button button = new Button("Initialize Testing Environment");
        panel.add(button);


        panel.add(new HTML("<br /><hr />"));

        topPanel.add(panel);
        button.addClickHandler(this);
        return panel;
    }

    public void addSelfTestClickHandler(ClickHandler handler) {
        selftestCheckBox.addClickHandler(handler);
    }

    public boolean isSelfTest() {
        return selftestCheckBox.getValue();
    }

    public abstract void handleClick(ClickEvent clickEvent);

    @Override
    public void onClick(ClickEvent clickEvent) {
        clear();
        handleClick(clickEvent);
    }

    // First element is button, rest is display material
    public void clear() {
        errorPanel.clear();
    }

    public void handleError(Throwable throwable) {
        ErrorHandler.handleError(panel, throwable);
    }

    /**
     * Display error messages
     * @param rawResponse
     * @param clas
     * @return errors occured
     */
    public boolean handleError(RawResponse rawResponse, Class clas) {
        if (ErrorHandler.handleError(errorPanel, rawResponse)) return true;
        if (rawResponse.getClass().equals(clas)) return false;
        ErrorHandler.handleError(errorPanel, rawResponse.getClass().getName(), clas);
        return true;
    }

    public FlowPanel panel() { return panel; }

    protected boolean isResetRequested() {
        return resetCheckBox != null && resetCheckBox.getValue();
    }

    public void handleMessages(Panel panel, AbstractOrchestrationResponse response) {
        for (MessageItem msg : response.getMessages()) {
            HTML h = new HTML("<p>" + msg.getMessage().replaceAll("\n", "<br />")  + "</p>");
            if (!msg.isSuccess()) {
                h.setStyleName("serverResponseLabelError");
            }
            panel.add(h);
        }

    }

}
