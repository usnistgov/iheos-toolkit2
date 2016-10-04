package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class LaunchInspectorClickHandler implements ClickHandler {
    private TestInstance testInstance;
    private String testSession;
    private SiteSpec siteSpec;

    public LaunchInspectorClickHandler(TestInstance testInstance, String testSession, SiteSpec siteSpec) {
        this.testInstance = testInstance;
        this.testSession = testSession;
        this.siteSpec = siteSpec;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.preventDefault();
        clickEvent.stopPropagation();

        launchInspectorTab(testInstance, testSession);
    }

    private void launchInspectorTab(final TestInstance testInstance, String testSession) {
        List<TestInstance> testInstances = new ArrayList<>();
        testInstances.add(testInstance);
        ClientUtils.INSTANCE.getToolkitServices().getTestResults(testInstances, testSession, new AsyncCallback<Map<String, Result>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Map<String, Result> resultMap) {
                MetadataInspectorTab itab = new MetadataInspectorTab();
                itab.setResults(resultMap.values());
                itab.setSiteSpec(siteSpec);
                itab.onTabLoad(true, "Test:" + testInstance.getId() );
            }
        });
    }

}
