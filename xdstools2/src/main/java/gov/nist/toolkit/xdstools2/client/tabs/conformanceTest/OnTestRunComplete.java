package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;

public abstract class OnTestRunComplete {
    abstract void updateDisplay(TestOverviewDTO testOverviewDTO, InteractionDiagramDisplay diagramDisplay);
}
