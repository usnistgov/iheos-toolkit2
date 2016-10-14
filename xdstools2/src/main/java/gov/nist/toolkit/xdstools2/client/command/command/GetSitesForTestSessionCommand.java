package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.Collection;

/**
 * Created by onh2 on 10/14/16.
 */
public abstract class GetSitesForTestSessionCommand extends GenericCommand<String,Collection<String>>{
    @Override
    public void run(String testSession){
        ClientUtils.INSTANCE.getToolkitServices().getSitesForTestSession(testSession,this);
    }
}
