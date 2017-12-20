package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirSearchRequest;

import java.util.List;

public abstract class FhirSearchCommand extends GenericCommand<FhirSearchRequest,List<Result>> {
    @Override
    public void run(FhirSearchRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().fhirSearch(var1,this);
    }
}
