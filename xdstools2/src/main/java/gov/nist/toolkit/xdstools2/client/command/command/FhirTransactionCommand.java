package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirTransactionRequest;

import java.util.List;

/**
 *
 */
public abstract class FhirTransactionCommand extends GenericCommand<FhirTransactionRequest, List<Result>> {
    @Override
    public void run(FhirTransactionRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().fhirTransaction(var1, this);
    }
}
