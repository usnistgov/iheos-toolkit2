package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSingleTestCommand;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSingleTestRequest;

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
        new DeleteSingleTestCommand(){
            @Override
            public void onComplete(TestOverviewDTO testOverviewDTO) {
                testRunner.removeTestDetails(testOverviewDTO.getTestInstance());
                testDisplayGroup.display(testOverviewDTO);
            }
        }.run(new DeleteSingleTestRequest(XdsTools2Presenter.data().getCommandContext(),testInstance));
    }
}
