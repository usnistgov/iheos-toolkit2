package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.GetStsSamlAssertionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetStsSamlAssertionMapCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionMapRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
class RunTestsClickHandler implements ClickHandler, TestIterator {
    private ConformanceTestTab conformanceTestTab;
    List<TestInstance> tests = new ArrayList<>();
    private TestsHeaderView testsHeaderView;
    private AbstractOrchestrationButton orchInit;
    private TestTarget testTarget;

    RunTestsClickHandler(ConformanceTestTab conformanceTestTab, TestTarget testTarget, TestsHeaderView testsHeaderView, AbstractOrchestrationButton orchInit, List<TestInstance> tests) {
        this.conformanceTestTab = conformanceTestTab;
        this.testTarget = testTarget;
        this.testsHeaderView = testsHeaderView;
        this.orchInit = orchInit;
        this.tests.addAll(tests);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.preventDefault();
        clickEvent.stopPropagation();

        testTarget.getSiteToIssueTestAgainst().setTls(orchInit.isTls());

        if (orchInit.isSaml()) {
            SiteSpec stsSpec = new SiteSpec("GazelleSts");
            TestInstance testInstance = new TestInstance("GazelleSts");
            testInstance.setSection("samlassertion-issue");
            Map<String, String> params = new HashMap<>();

            if (orchInit.isXuaOption()) {
                testTarget.getSiteToIssueTestAgainst().setSaml(true);

                try {
                    new GetStsSamlAssertionMapCommand() {
                        @Override
                        public void onComplete(Map<String, String> result) {
                            orchInit.setSamlAssertionsMap(result);
                            onDone(null);
                        }
                    }.run(new GetStsSamlAssertionMapRequest(conformanceTestTab.getCommandContext(),testInstance,stsSpec,params));

                } catch (Exception ex) {
                    testsHeaderView.showRunningMessage(false);
                }
            } else {
                // Get STS SAML Assertion once for the entire test collection
                String xuaUsername = "Xuagood";
                params.put("$saml-username$", xuaUsername);
                try {
                    new GetStsSamlAssertionCommand() {
                        @Override
                        public void onComplete(String result) {
                            testTarget.getSiteToIssueTestAgainst().setSaml(true);
                            testTarget.getSiteToIssueTestAgainst().setStsAssertion(result);

                            onDone(null);
                        }
                    }.run(new GetStsSamlAssertionRequest(conformanceTestTab.getCommandContext(), xuaUsername, testInstance, stsSpec, params));
                } catch (Exception ex) {
                    testsHeaderView.showRunningMessage(false);
                    new PopupMessage("runAll: Client call failed: getStsSamlAssertion: " + ex.toString());
                }
            }
        } else {
            // No SAML
            testTarget.getSiteToIssueTestAgainst().setSaml(false);
            onDone(null);
        }

    }

    @Override
    public void onDone(TestInstance unused) {
        testsHeaderView.showRunningMessage(true);
        if (tests.size() == 0) {
            testsHeaderView.showRunningMessage(false);
            return;
        }
        TestInstance next = tests.get(0);
        tests.remove(0);

        if (orchInit.isXuaOption() && orchInit.getSamlAssertionsMap() != null) {
            setXuaOptionSamlAssertion(next);
        }

        conformanceTestTab.runTest(next, null, this);
    }

    private void setXuaOptionSamlAssertion(TestInstance testInstance) {

        String samlAssertion = orchInit.getSamlAssertionsMap().get(conformanceTestTab.getXuaUsernameFromTestplan(testInstance));
        conformanceTestTab.getSiteToIssueTestAgainst().setSaml(true);
        conformanceTestTab.getSiteToIssueTestAgainst().setStsAssertion(samlAssertion);
    }
}
