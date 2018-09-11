This uses a GetFolderAndContents stored query using the parameters, $XDSFolderUniqueId and $MetadataLevel = 2, to return both the original and updated version of the Folder.

Since each DocumentEntry (there are two of them) is linked to both Folders, there will be four HasMember Associations in the results.
 
The following validations are run on the query results:
 
* same_logicalId - both versions have the same logicalId
* different_id - entryUUID attribute on each version is different 
* orig_is_version_1 - the original version has version = 1
* update_is_version_2 - the updated version has version = 2
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original version 
* update_id_and_lid_different - the entryUUID and logicalId attributes have different values for the updated version 
* uniqueid_same - the uniqueId attribute is the same for both versions
* original_is_deprecated - the original version of the Folder has an availabilityStatus of Deprecated
* update_is_approved - the updated version of the Folder has an availabilityStatus of Approved 
