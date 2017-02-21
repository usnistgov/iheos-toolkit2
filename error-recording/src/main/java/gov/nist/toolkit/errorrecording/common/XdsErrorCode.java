package gov.nist.toolkit.errorrecording.common;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * IMPORTANT
 * Using structures from this class:
 * Have an ErrorRecorderClass that implements the ErrorRecorder interface. The new ErrorRecorderClass must import
 * package gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code; instead of
 * gov.nist.toolkit.errorrecording.client.XdsErrorCode or the compilator does not find the declaration of the Code enum
 * and throws an error.
 *
 */
public class XdsErrorCode implements IsSerializable  {

	static public enum Code implements IsSerializable {
		NoCode,
		SoapFault,
		XDSMissingDocument,
		XDSMissingDocumentMetadata,
		XDSRegistryNotAvailable,
		XDSRegistryError,
		XDSRepositoryError,
		XDSRegistryDuplicateUniqueIdInMessage,
		XDSRepositoryDuplicateUniqueIdInMessage,
		XDSDuplicateUniqueIdInRegistry,
		XDSNonIdenticalHash,
		XDSRegistryBusy,
		XDSRepositoryBusy,
		XDSRegistryOutOfResources,
		XDSRepositoryOutOfResources,
		XDSRegistryMetadataError,
		XDSRepositoryMetadataError,
		XDSTooManyResults,
		XDSExtraMetadataNotSaved,
		XDSUnknownPatientId,
		XDSPatientIdDoesNotMatch,
		XDSUnknownStoredQuery,
		XDSStoredQueryMissingParam,
		XDSStoredQueryParamNumber,
		XDSRegistryDeprecatedDocumentError,
		XDSUnknownRepositoryId,
		XDSDocumentUniqueIdError,
		XDSPartialSuccess,
		XDSMetadataVersionError,
		XDSMetadataUpdateOperationError,
		XDSMetadataUpdateError,
		XDSMissingHomeCommunityId,
		XDSUnknownCommunity,
		XDSResultNotSinglePatient
	};
	
	
	
}
