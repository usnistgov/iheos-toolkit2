Use the GetFolders query to verify both versions of the Folder object are found in the Document Registry and have an availabilityStatus of Deprecated.
The following validations are run on the query results: 
* same_logicalId - both versions of the Folder object have same logicalId 
* different_id - entryUUID attribute for each Folder object is different 
* orig_is_version_1 - the original version is version = 1 
* update_is_version_2 - the updated version is version = 2 
* both_are_deprecated - both versions of the Folder object has an availabilityStatus of Deprecated 
