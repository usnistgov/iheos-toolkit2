package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.toolkitServicesCommon.SimConfigResource;
import gov.nist.toolkit.toolkitServicesCommon.SimId;
import gov.nist.toolkit.toolkitServicesCommon.SimIdResource;
import org.apache.log4j.Logger;

/**
 * Not for public use.
 */

public class ToolkitFactory {
    static Logger logger = Logger.getLogger(ToolkitFactory.class);

    static public gov.nist.toolkit.actorfactory.client.SimId asServerSimId(SimId simId) {
        return new gov.nist.toolkit.actorfactory.client.SimId(simId.getUser(), simId.getId(), simId.getActorType(), simId.getEnvironmentName());
    }

    static public SimIdResource asSimIdBean(gov.nist.toolkit.actorfactory.client.SimId simId) {
        SimIdResource bean = new SimIdResource();
        bean.setId(simId.getId());
        bean.setUser(simId.getUser());
        bean.setActorType(simId.getActorType());
        bean.setEnvironmentName(simId.getEnvironmentName());
        return bean;
    }

    static public SimConfigResource asSimConfigBean(SimulatorConfig config) {
        SimConfigResource bean = new SimConfigResource();
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
