package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 11/16/18.
 */
public class GetOrchestrationPropertiesRequest extends CommandContext {
    TestSession testSession;
    String actorTypeShortName;


    public GetOrchestrationPropertiesRequest(){}
    public GetOrchestrationPropertiesRequest(CommandContext context, TestSession testSession, String actorTypeShortName){
        copyFrom(context);
        this.testSession = testSession;
        this.actorTypeShortName = actorTypeShortName;
    }

    @Override
    public TestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(TestSession testSession) {
        this.testSession = testSession;
    }

    public String getActorTypeShortName() {
        return actorTypeShortName;
    }

    public void setActorTypeShortName(String actorTypeShortName) {
        this.actorTypeShortName = actorTypeShortName;
    }
}
