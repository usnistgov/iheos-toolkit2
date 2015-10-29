package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public class ToolkitFactory {

    static public SimId newSimId(String id, String user, String actorType, String environmentName) {
        SimIdResource bean =  new SimIdResource();
        bean.setId(id);
        bean.setUser(user);
        bean.setActorType(actorType);
        bean.setEnvironmentName(environmentName);
        return bean;
    }

    static public SendRequest newSendRequest(SimId simId) { return new SendRequestResource(simId); }
}
