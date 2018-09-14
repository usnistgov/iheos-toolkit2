Use the GetDocuments query to verify both versions of the DocumentEntry object are found in the Document Registry and have an availabilityStatus of Deprecated.

The following validations are run on the query results: 
* same_logicalId - both versions of the DocumentEntry object have same logicalId 
* different_id - entryUUID attribute for each DocumentEntry object is different 
* orig_is_version_1 - the original version has version = 1 
* update_is_version_2 - the updated version has version = 2 
* orig_id_and_lid_same – the entryUUID and logicalId attributes have the same value for the original DocumentEntry object
* update_id_and_lid_different - the entryUUID and logicalId attributes are different for the updated DocumentEntry object 
* uniqueid_same - the uniqueId attribute on both DocumentEntry objects are the same 
* original_is_deprecated - the initial version of the DocumentEntry object has an availabilityStatus of Deprecated 
* update_is_approved - the update version of the DocumentEntry has an availabilityStatus of Approved 

