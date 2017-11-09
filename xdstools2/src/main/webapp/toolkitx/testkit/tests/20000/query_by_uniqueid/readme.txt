This step uses the query, GetFolders, to fetch the Folder objects by the common uniqueId.  The query parameter, $MetadataLevel, is set to two (2) to allow both versions of the Folder object to be returned.
 
The following validations are run on the query results:
 
* same_logicalId - both version have same logicalId 
* different_id - entryUUID is different for each version
* orig_is_version_1 - the original version has value of 1 
* update_is_version_2 - the updated version has value of 2 
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original version of the Folder
* update_id_and_lid_different - the entryUUID and logicalId attributes have the different values for the latest version of the Folder 
* uniqueid_same - the uniqueId attribute on both Folder are the same 
* original_is_deprecated - the original version of the Folder must have availabilityStatus of Deprecated
* update_is_approved - the updated of the Folder must have availabilityStatus of Approved


