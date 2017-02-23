package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionMapRequest;

import java.util.Map;

/**
 * Created by skb1 on 1/17/17.
 */
public abstract class GetStsSamlAssertionMapCommand extends GenericCommand<GetStsSamlAssertionMapRequest,Map<String,String>>{
    @Override
    public void run(GetStsSamlAssertionMapRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getStsSamlAssertionsMap(var1,this);
//        ClientUtils.INSTANCE.getToolkitServices().getStsSamlAssertionsMap(orchInit.getStsTestInstance(), orchInit.getStsSpec() , orchInit.getSamlParams(), this);
    }
}
