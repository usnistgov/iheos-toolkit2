Use the GetFolders query to verify that the previous step did not alter the original Folder object in the Document Registry.
The following validations are run on the query results: 
* orig_is_version_1 - the original version is version = 1 
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original Folder object
* original_is_approved - the initial version of the Folder object has an availabilityStatus of Approved
