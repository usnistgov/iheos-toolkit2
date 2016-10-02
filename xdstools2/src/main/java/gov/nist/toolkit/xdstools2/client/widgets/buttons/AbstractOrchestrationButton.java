package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.ErrorHandler;

/**
 *
 */
abstract public class AbstractOrchestrationButton implements ClickHandler {
    private final VerticalPanel panel = new VerticalPanel();  // top panel
    private Panel topPanel;
    private String label = null;
    private String resetLabel = null;
    private CheckBox resetCheckBox = null;
    private Panel customPanel = null;

    public AbstractOrchestrationButton(Panel topPanel, String label) {
        this.topPanel = topPanel;
        this.label = label;
        build();
    }

    protected AbstractOrchestrationButton() {}

    protected void setParentPanel(Panel parent) {
        this.topPanel = parent;
    }

    protected void setLabel(String label) {
        this.label = label;
    }

    protected void setCustomPanel(Panel panel) {
        this.customPanel = panel;
    }

    protected void setResetLabel(String resetLabel) {
        this.resetLabel = resetLabel;
    }

    public Panel build() {
        panel.add(new HTML("<hr /><h2>Test Setup</h2><p>The test setup needs to be initialized before tests can be run."));

        if (customPanel != null) {
            panel.add(customPanel);
        }

        if (resetLabel != null) {
            resetCheckBox = new CheckBox(resetLabel);
            panel.add(resetCheckBox);
        }
        final Button button = new Button(label);
        panel.add(button);

        panel.add(new HTML("<br /><hr />"));

        topPanel.add(panel);
        button.addClickHandler(this);
        return panel;
    }

    public abstract void handleClick(ClickEvent clickEvent);

    @Override
    public void onClick(ClickEvent clickEvent) {
        clear();
        handleClick(clickEvent);
    }

    // First element is button, rest is display material
    public void clear() {
//        panel.clear();
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
        if (ErrorHandler.handleError(panel, rawResponse)) return true;
        if (rawResponse.getClass().equals(clas)) return false;
        ErrorHandler.handleError(panel, rawResponse.getClass().getName(), clas);
        return true;
    }

    public VerticalPanel panel() { return panel; }

    protected boolean isResetRequested() {
        return resetCheckBox != null && resetCheckBox.getValue();
    }

}
