package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 * Created by onh2 on 10/14/16.
 */
public abstract class ClearTestSessionCommand extends GenericCommand<String,String>{
    @Override
    public void run(String testSession){
        ClientUtils.INSTANCE.getToolkitServices().clearTestSession(testSession,this);
    }
}
