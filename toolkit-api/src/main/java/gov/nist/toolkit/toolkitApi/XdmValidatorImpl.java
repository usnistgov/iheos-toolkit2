package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmReport;
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmRequest;

/**
 *
 */
public class XdmValidatorImpl extends AbstractActor implements XdmValidator {
    @Override
    public XdmReport validate(XdmRequest request) throws ToolkitServiceException {
        return engine.validateXDM(request);
    }
}
