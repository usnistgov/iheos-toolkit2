This uses a GetFolders query to confirm only one Folder is returned and the availability status remains Approved. 

The following validations are run on the query results:
 
* different_id - entryUUID is different for each version
* orig_is_version_1 - the original version has value of 1 
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original version of the Folder
* original_is_approved - the original version of the Folder must have availabilityStatus of Approved
