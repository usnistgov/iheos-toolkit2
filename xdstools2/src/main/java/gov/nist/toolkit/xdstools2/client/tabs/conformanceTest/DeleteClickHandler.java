package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
public class DeleteClickHandler implements ClickHandler {
    private TestInstance testInstance;
    private TestContext testContext;
    private TestRunner testRunner;
    private TestDisplayGroup testDisplayGroup;

    public DeleteClickHandler(TestDisplayGroup testDisplayGroup, TestContext testContext, TestRunner testRunner, TestInstance testInstance) {
        this.testDisplayGroup = testDisplayGroup;
        this.testContext = testContext;
        this.testRunner = testRunner;
        this.testInstance = testInstance;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.preventDefault();
        clickEvent.stopPropagation();
        ClientUtils.INSTANCE.getToolkitServices().deleteSingleTestResult(testContext.getTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(TestOverviewDTO testOverviewDTO) {
//                testRunner.displayTest(testsPanel, testDisplayGroup, testOverviewDTO);
                testRunner.removeTestDetails(testOverviewDTO.getTestInstance());
                testDisplayGroup.display(testOverviewDTO);
            }
        });
    }
}
