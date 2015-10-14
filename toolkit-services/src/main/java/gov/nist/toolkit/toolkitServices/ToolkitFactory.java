package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;

/**
 * Created by bill on 10/9/15.
 */
public class ToolkitFactory {

    static public gov.nist.toolkit.actorfactory.client.SimId asServerSimId(SimIdBean simId) {
        return new gov.nist.toolkit.actorfactory.client.SimId(simId.getUser(), simId.getId(), simId.getActorType(), simId.getEnvironmentName());
    }

    static public SimIdBean asSimIdBean(gov.nist.toolkit.actorfactory.client.SimId simId) {
        SimIdBean bean = new SimIdBean();
        bean.setId(simId.getId());
        bean.setUser(simId.getUser());
        bean.setActorType(simId.getActorType());
        bean.setEnvironmentName(simId.getEnvironmentName());
        return bean;
    }

}
