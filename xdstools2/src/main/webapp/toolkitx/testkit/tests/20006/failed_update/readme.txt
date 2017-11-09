Test the ability of the Document Registry to detect both conditions with a transaction:
* Metadata Update operation for a new version of the Document Entry with Association Propagation set to "yes".
* Submit Association for an APND relationship between both Document Entry objects.

This transaction should fail with error: XDSMetadataUpdateOperationError. 

