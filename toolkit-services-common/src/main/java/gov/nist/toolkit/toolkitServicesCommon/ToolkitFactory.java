package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Created by bill on 10/9/15.
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
