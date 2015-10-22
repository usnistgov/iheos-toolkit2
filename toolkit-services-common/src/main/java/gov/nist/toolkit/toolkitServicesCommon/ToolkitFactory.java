package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public class ToolkitFactory {

    static public SimId newSimId(String id, String user, String actorType, String environmentName) {
        SimIdBean bean =  new SimIdBean();
        bean.setId(id);
        bean.setUser(user);
        bean.setActorType(actorType);
        bean.setEnvironmentName(environmentName);
        return bean;
    }

}
