package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmReport;
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmRequest;

/**
 *
 */
public interface XdmValidator {
    XdmReport validate(XdmRequest request) throws ToolkitServiceException;
}
