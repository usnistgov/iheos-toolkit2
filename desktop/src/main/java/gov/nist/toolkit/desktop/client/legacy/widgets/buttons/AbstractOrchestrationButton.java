package gov.nist.toolkit.desktop.client.legacy.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.ErrorHandler;
import gov.nist.toolkit.desktop.client.commands.GetStsSamlAssertionCommand;
import gov.nist.toolkit.desktop.shared.command.request.GetStsSamlAssertionRequest;
import gov.nist.toolkit.desktop.client.commands.GetToolkitPropertiesCommand;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.MessageItem;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

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
    private Map<String,String> samlAssertionsMap;
    private CheckBox tlsCheckBox;
    private CheckBox selftestCheckBox = new CheckBox("Self Test - Enable self test mode.");
    private String label = null;
    private String resetLabel = null;
    private String systemDiagramUrl = null;

    private CheckBox samlCheckBox = new CheckBox("SAML");
    private boolean xuaOption;
    private String samlAssertion;
    private static final TestInstance stsTestInstance = new TestInstance("GazelleSts");
    static {
        stsTestInstance.setSection("samlassertion-issue");
    }
    private static final SiteSpec stsSpec =  new SiteSpec("GazelleSts");
    private static final Map<String, String> samlParams = new HashMap<>();
    static public final String XUA_OPTION = "xua";

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
        return build(true);
    }

    public Panel build(boolean enableReset) {
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

        if (enableReset) {
            String resetLabel = "Reset - Initialize will delete all supporting simulators and Patient IDs and recreate them.";
            resetCheckBox = new CheckBox(resetLabel);
            panel.add(resetCheckBox);
            panel.add(new HTML("<br />"));
        }

//        panel.add(selftestCheckBox);
//        panel.add(new HTML("<br />"));

        if (isXuaOption()) { // Xua Option has SAML ON by default
            samlCheckBox.setValue(true);
            samlCheckBox.setEnabled(false);
        } else {
            samlCheckBox.setTitle("Uses Gazelle STS Username 'Xuagood'");
        }
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
        new GetToolkitPropertiesCommand(){
            @Override
            public void onComplete(Map<String, String> tkPropMap) {
                if (Boolean.parseBoolean(tkPropMap.get("Enable_SAML"))) { // Master flag
                    samlCheckBox.setVisible(true);
                    samlCheckBox.setStyleName("orchestrationOption");
                }
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());

    }

    public void addSelfTestClickHandler(ClickHandler handler) {
        selftestCheckBox.addClickHandler(handler);
    }

    public boolean isSelfTest() {
        return selftestCheckBox.getValue();
    }

    public abstract void orchestrate();

    public void handleClick(ClickEvent event) {
        if (isSaml() || isXuaOption()) {
            // Get STS SAML Assertion
            String xuaUsername = "Xuagood";
            getSamlParams().put("$saml-username$",xuaUsername);
            try {

                new GetStsSamlAssertionCommand(){
                    @Override
                    public void onComplete(String result) {
                        setSamlAssertion(result);
                        orchestrate();
                    }
                }.run(new GetStsSamlAssertionRequest(ClientUtils.INSTANCE.getCurrentCommandContext(), xuaUsername, getStsTestInstance(), getStsSpec(), getSamlParams()));
            } catch (Throwable t) {}
        } else {
            orchestrate();
        }
    }

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
        siteSpec.setSaml((isSaml()) && getSamlAssertion()!=null); // Does the SAML assertion really exist?
        siteSpec.setStsAssertion(getSamlAssertion());
    }

    public boolean isXuaOption() {
        return xuaOption;
    }

    public void setXuaOption(boolean xuaOption) {
        this.xuaOption = xuaOption;
        if (xuaOption) {
            samlCheckBox.setValue(true);
            samlCheckBox.setEnabled(false);
            samlCheckBox.setTitle("");
        } else {
            samlCheckBox.setValue(false);
            samlCheckBox.setEnabled(true);
        }
    }

    public Map<String, String> getSamlAssertionsMap() {
        return samlAssertionsMap;
    }

    public void setSamlAssertionsMap(Map<String, String> samlAssertionsMap) {
        this.samlAssertionsMap = samlAssertionsMap;
    }

    public static Map<String, String> getSamlParams() {
        return samlParams;
    }

    public static SiteSpec getStsSpec() {
        return stsSpec;
    }

    public static TestInstance getStsTestInstance() {
        return stsTestInstance;
    }
}
