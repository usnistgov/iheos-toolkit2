package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.toolkitServicesCommon.SimConfigBean;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
import org.apache.log4j.Logger;

/**
 * Created by bill on 10/9/15.
 */
public class ToolkitFactory {
    static Logger logger = Logger.getLogger(ToolkitFactory.class);

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

    static public SimConfigBean asSimConfigBean(SimulatorConfig config) {
        SimConfigBean bean = new SimConfigBean();
        bean.setId(config.getId().getId());
        bean.setUser(config.getId().getUser());
        bean.setActorType(config.getActorType());

        for (SimulatorConfigElement ele : config.getElements()) {
            if (ele.isBoolean()) {
                bean.setProperty(ele.name, ele.asBoolean());
            } else if (ele.isString()) {
                bean.setProperty(ele.name, ele.asString());
            }
        }
        return bean;
    }

}
