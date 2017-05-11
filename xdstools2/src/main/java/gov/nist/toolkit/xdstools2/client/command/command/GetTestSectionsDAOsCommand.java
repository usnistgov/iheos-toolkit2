package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestSectionsDAOsRequest;

import java.util.List;

/**
 * Command requesting the server for test sections.
 * Created by onh2 on 10/31/16.
 */
public abstract class GetTestSectionsDAOsCommand extends GenericCommand<GetTestSectionsDAOsRequest,List<SectionDefinitionDAO>> {
    @Override
    public void run(GetTestSectionsDAOsRequest request) {
        FrameworkInitialization.data().getToolkitServices().getTestSectionsDAOs(request,this);
    }
}
