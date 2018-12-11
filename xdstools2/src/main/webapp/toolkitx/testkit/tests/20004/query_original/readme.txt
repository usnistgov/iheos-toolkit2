Use the GetFolders query to verify the submission of the Folder to the Document Registry in the previous step. 
The following validations are run on the query results: 
* orig_is_version_1 - the original version is version = 1 
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original Folder object
* original_is_approved - the original version of the Folder object has an availabilityStatus of Approved 

