package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestdataSetListingRequest;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetTestdataSetListingCommand extends GenericCommand<GetTestdataSetListingRequest,List<String>>{
    @Override
    public void run(GetTestdataSetListingRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getTestdataSetListing(var1,this);
    }
}
