package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.services.client.RgOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

/**
 *
 */
public class BuildRepTestOrchestrationButton extends ReportableButton {
    private ConformanceTestTab testTab;

    public BuildRepTestOrchestrationButton(ConformanceTestTab testTab, Panel topPanel, String label) {
        super(topPanel, label);
        this.testTab = testTab;
    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }

        // clear previous display
        clean();

        RepOrchestrationRequest request = new RepOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());

        testTab.toolkitService.buildRepTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RgOrchestrationResponse.class)) return;
                RepOrchestrationResponse orchResponse = (RepOrchestrationResponse) rawResponse;
                testTab.setRepOrchestrationResponse(orchResponse);
                panel().add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    panel().add(new HTML("<h3>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</h3>"));
                }

                FlexTable table = new FlexTable();
                panel().add(table);
            }
        });
    }
}
