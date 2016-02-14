package gov.nist.toolkit.toolkitServices

import groovy.transform.TypeChecked;

/**
 *
 */
@TypeChecked
public class QueryParametersManager {

    static public gov.nist.toolkit.simulators.sim.cons.QueryParameters internalize(gov.nist.toolkit.toolkitServicesCommon.resource.QueryParameters external) {
        gov.nist.toolkit.simulators.sim.cons.QueryParameters internal= new gov.nist.toolkit.simulators.sim.cons.QueryParameters();

        external.parameterNames.each { name ->
            internal.addParameter(name, external.getValues(name))
        }
        return internal
    }
}
