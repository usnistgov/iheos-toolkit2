package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsOverviewCommand;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.ArrayList;
import java.util.List;

public class TestBarOpenHandler implements OpenHandler<DisclosurePanel> {
    TestDisplay testDisplay;
    TestOverviewDTO testOverview;
    InteractionDiagramDisplay diagramDisplay;
    CommandContext commandContext;
    SimpleCallbackT<TestOverviewDTO> consumer;

    public TestBarOpenHandler(TestDisplay testDisplay, TestOverviewDTO testOverview, CommandContext commandContext, InteractionDiagramDisplay diagramDisplay, SimpleCallbackT<TestOverviewDTO> consumer) {
        this.testDisplay = testDisplay;
        this.testOverview = testOverview;
        this.diagramDisplay = diagramDisplay;
        this.commandContext = commandContext;
        this.consumer = consumer;
    }

    @Override
    public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
        try {
            List<TestInstance> ti = new ArrayList<>();
            ti.add(testOverview.getTestInstance()); // Only one TI
            GetTestsOverviewRequest gtor = new GetTestsOverviewRequest(commandContext, ti);
            new GetTestsOverviewCommand() {
                @Override
                public void onComplete(List<TestOverviewDTO> result) {
                    TestOverviewDTO toDTO = result.get(0); // Expect only one since only one test instance was requested
                    if (consumer!=null) {
                        consumer.run(toDTO);
                    }
                    if (diagramDisplay!=null) {
                        diagramDisplay.setTestOverviewDTO(toDTO);
                    }

                    testDisplay.display(toDTO, diagramDisplay);
                    testDisplay.getView().autoOpenIfOnlyOneSection();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    super.onFailure(throwable);
                }
            }.run(gtor);
            // This is a one-time handler. Removing the handler will prevent a reload of the same TestOverviewDTO.
            if (testDisplay.getView().getOpenTestBarHReg() != null) {
                testDisplay.getView().getOpenTestBarHReg().removeHandler();
            }
        } catch (Exception ex) {
            GWT.log(ex.toString());
        }
    }
}

