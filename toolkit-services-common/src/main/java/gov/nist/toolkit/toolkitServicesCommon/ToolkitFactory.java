package gov.nist.toolkit.toolkitServicesCommon;

import gov.nist.toolkit.toolkitServicesCommon.resource.SimIdResource;

/**
 *
 */
public class ToolkitFactory {

    static public SimId newSimId(String id, String user, String actorType, String environmentName) {
        SimIdResource bean = new SimIdResource();
        bean.setId(id);
        bean.setUser(user);
        bean.setActorType(actorType);
        bean.setEnvironmentName(environmentName);
        return bean;
    }

    static public SimId newSimId(String id, String user, String actorType, String environmentName, boolean fhir) {
        SimIdResource bean = new SimIdResource();
        bean.setId(id);
        bean.setUser(user);
        bean.setActorType(actorType);
        bean.setEnvironmentName(environmentName);
        if (fhir) bean.forFhir();
        return bean;
    }

}
