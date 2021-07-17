package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirCreateRequest;

import java.util.List;

/**
 *
 */
public abstract class FhirCreateCommand extends GenericCommand<FhirCreateRequest, List<Result>> {
    @Override
    public void run(FhirCreateRequest var1) {
//        ClientUtils.INSTANCE.getToolkitServices().fhirCreate(var1, this);
    }
}
