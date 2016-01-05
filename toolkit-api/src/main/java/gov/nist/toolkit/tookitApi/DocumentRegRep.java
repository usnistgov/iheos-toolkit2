package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.ObjectRefList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

/**
 *
 */
public interface DocumentRegRep  extends SimConfig {

    ObjectRefList findDocumentsForPatientID(String patientID) throws ToolkitServiceException;
    String getDocEntry(String id) throws ToolkitServiceException;
}
