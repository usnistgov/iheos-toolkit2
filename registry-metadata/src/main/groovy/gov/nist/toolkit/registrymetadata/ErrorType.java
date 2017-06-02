package gov.nist.toolkit.registrymetadata;

/**
 *
 */
public enum ErrorType {
    None,
    LeavesEmptySubmissionSet,   // Not defined in Profile
    ObjectNotInDeletionSet, // Not defined in Profile
    UnresolvedReferenceException,
    RPLCCannotBeDeleted
}
