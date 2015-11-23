package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.ObjectRefList;

/**
 *
 */
public interface DocumentRegRep  extends AbstractActorInterface {

    ObjectRefList findDocumentsForPatientID(String patientID) throws ToolkitServiceException;
    String getDocEntry(String id) throws ToolkitServiceException;
}
