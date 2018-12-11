Use the GetDocuments query to verify the submission of the DocumentEntry to the Document Registry in the previous step. 
The following validations are run on the query results: 
* orig_is_version_1 - the original version has version = 1 
* orig_id_and_lid_same - the entryUUID and logicalId attributes have the same value for the original DocumentEntry object
* original_is_approved - the initial version of the DocumentEntry object has an availabilityStatus of Approved 

