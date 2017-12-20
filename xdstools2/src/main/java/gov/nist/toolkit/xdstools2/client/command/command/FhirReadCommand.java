package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirReadRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirTransactionRequest;

import java.util.List;

/**
 *
 */
public abstract class FhirReadCommand extends GenericCommand<FhirReadRequest, List<Result>> {
    @Override
    public void run(FhirReadRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().fhirRead(var1, this);
    }
}
