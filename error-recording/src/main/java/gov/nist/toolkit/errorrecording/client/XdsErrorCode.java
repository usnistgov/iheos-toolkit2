package gov.nist.toolkit.errorrecording.client;

import com.google.gwt.user.client.rpc.IsSerializable;

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
		XDSResultNotSinglePatient,
		XDSIRequestError,
		XDSIUnknownIdsUid,
		ReferencesExistException,
		XDSUnreferencedObjectException,
		UnresolvedReferenceException;
    };

	public static Code fromString(String text) {
		for (Code b : Code.values()) {
			if (b.name().equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
	
}
