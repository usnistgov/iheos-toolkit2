package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.List;

public abstract class GetTestsResultEnvelopeCommand extends GenericCommand<GetTestsOverviewRequest,List<TestOverviewDTO>> {
    @Override
    public void run(GetTestsOverviewRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTestsResultEnvelope(request,this);
    }
}
