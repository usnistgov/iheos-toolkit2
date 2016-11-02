package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.MessageItem;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.ErrorHandler;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.HashMap;
import java.util.Map;

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
    private CheckBox samlCheckBox = new CheckBox("SAML");
    private String samlAssertion;
    private CheckBox tlsCheckBox;
    private String label = null;
    private String resetLabel = null;
    private String systemDiagramUrl = null;

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

    protected void setSystemDiagramUrl(String url) { this.systemDiagramUrl = url; }

    public Panel build() {
        if (!errorPanelAdded) {
            errorPanelAdded = true;
            panel.add(errorPanel);
        }
        panel.add(new HTML("<hr /><h2>Testing Environment</h2>"));

        if (systemDiagramUrl != null) {
            Image initiatingGatewayDiagram=new Image();
            initiatingGatewayDiagram.setUrl(systemDiagramUrl);
            initiatingGatewayDiagram.setHeight("300px");
            panel.add(initiatingGatewayDiagram);
            panel.add(new HTML("<br />"));
        }

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

        samlCheckBox.setTitle("Uses Gazelle STS Username 'Xuagood'");
        panel.add(samlCheckBox);
        enableSaml();

        tlsCheckBox = new CheckBox("TLS");
        tlsCheckBox.setStyleName("orchestrationOption");
        panel.add(tlsCheckBox);

        final Button button = new Button("Initialize Testing Environment");
        panel.add(button);

        panel.add(new HTML("<br /><hr />"));

        topPanel.add(panel);

        button.addClickHandler(this);
        return panel;
    }

    private void enableSaml() {
        samlCheckBox.setVisible(false);
        ClientUtils.INSTANCE.getToolkitServices().getToolkitProperties(new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("AOB: Error getting properties for SAML selector display: " + throwable.toString());
            }

            @Override
            public void onSuccess(final Map<String, String> tkPropMap) {
                if (Boolean.parseBoolean(tkPropMap.get("Enable_SAML"))) { // Master flag
                    samlCheckBox.setVisible(true);
                    samlCheckBox.setStyleName("orchestrationOption");

                    // Get STS SAML Assertion
                    TestInstance testInstance = new TestInstance("GazelleSts");
                    testInstance.setSection("samlassertion-issue");
                    SiteSpec stsSpec =  new SiteSpec("GazelleSts");
                    Map<String, String> params = new HashMap<>();
                    String xuaUsername = "Xuagood";
                    params.put("$saml-username$",xuaUsername);
                    try {
                        ClientUtils.INSTANCE.getToolkitServices().getStsSamlAssertion(xuaUsername, testInstance, stsSpec, params, new AsyncCallback<String>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                new PopupMessage("AOB: getStsSamlAssertion call failed: " + throwable.toString());
                            }
                            @Override
                            public void onSuccess(String s) {
                                setSamlAssertion(s);
                            }
                        });
                    } catch (Exception ex) {
                        new PopupMessage("AOB: Client call failed: getStsSamlAssertion: " + ex.toString());
                    }
                }
            }
        });

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

    public boolean isSaml() {
        return (samlCheckBox!=null && samlCheckBox.getValue());
    }

    public boolean isTls() {
        return (tlsCheckBox!=null && tlsCheckBox.getValue());
    }

    public void addSamlValueChangeHanlder(ValueChangeHandler valueChangeHandler) {
        samlCheckBox.addValueChangeHandler(valueChangeHandler);
    }

    public String getSamlAssertion() {
        return samlAssertion;
    }

    public void setSamlAssertion(String samlAssertion) {
        this.samlAssertion = samlAssertion;
    }

    public void setSamlAssertion(SiteSpec siteSpec) {
       siteSpec.setSaml(true);
       siteSpec.setStsAssertion(getSamlAssertion());
    }
}
