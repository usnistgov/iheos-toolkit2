package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.services.client.RgOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

/**
 *
 */
class BuildRGTestOrchestrationButton extends ReportableButton {
    private RGTestTab testTab;
    SiteSpec siteUnderTest;
    boolean useExposedRR;
    boolean useSimAsSUT;

    BuildRGTestOrchestrationButton(RGTestTab testTab, Panel topPanel, String label) {
        super(topPanel, label);
        this.testTab = testTab;
    }

    public void handleClick(ClickEvent event) {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }

        useExposedRR = testTab.usingExposedRR();
        useSimAsSUT = testTab.useSimAsSUT();
        siteUnderTest = testTab.getSiteSelection();

        RgOrchestrationRequest request = new RgOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
//        request.setEnvironmentName(??????);
        request.setSiteUnderTest(siteUnderTest);
        request.setUseExposedRR(useExposedRR);
        request.setUseSimAsSUT(useSimAsSUT);

        testTab.toolkitService.buildRgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RgOrchestrationResponse.class)) return;
                RgOrchestrationResponse orchResponse = (RgOrchestrationResponse) rawResponse;

            }
        });
    }
}
