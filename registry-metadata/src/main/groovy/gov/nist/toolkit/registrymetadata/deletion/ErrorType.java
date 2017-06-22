package gov.nist.toolkit.registrymetadata.deletion;

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
