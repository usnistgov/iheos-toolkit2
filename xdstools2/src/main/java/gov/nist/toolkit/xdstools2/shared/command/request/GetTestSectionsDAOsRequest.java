package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Request context for {@link gov.nist.toolkit.xdstools2.client.command.command.GetTestSectionsDAOsCommand GetTestSectionsDAOsCommand}
 * call to the server.
 * Created by onh2 on 10/31/16.
 */
public class GetTestSectionsDAOsRequest extends CommandContext{
    private TestInstance testInstance;

    /**
     * Default constructor only here for serialization,
     * prefer using {@link #GetTestSectionsDAOsRequest(CommandContext, TestInstance)}.
     */
    public GetTestSectionsDAOsRequest(){}

    /**
     * Constructor that should be used for any GetTestSectionsDAOs request.
     * @param context CommandContext containing the environment and the test session name.
     * @param testInstance test instance.
     */
    public GetTestSectionsDAOsRequest(CommandContext context, TestInstance testInstance){
        copyFrom(context);
        this.testInstance = testInstance;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }
}
