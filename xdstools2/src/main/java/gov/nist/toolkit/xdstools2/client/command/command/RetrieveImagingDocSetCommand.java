package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.RetrieveImagingDocSetRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class RetrieveImagingDocSetCommand extends GenericCommand<RetrieveImagingDocSetRequest,List<Result>>{
    @Override
    public void run(RetrieveImagingDocSetRequest var1) {
        FrameworkInitialization.data().getToolkitServices().retrieveImagingDocSet(var1,this);
    }
}
