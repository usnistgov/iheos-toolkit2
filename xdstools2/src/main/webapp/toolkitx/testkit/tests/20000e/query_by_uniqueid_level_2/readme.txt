This uses a GetFolderAndContents query setting the parameters, $XDSFolderEntryUUID, using the same XDSFolder.entryUUID used in the step, update_no_ap.  The parameter, $MetadataLevel, equals two (2) allowing both versions of the Folder object to be returned.  
Also, this allows for confirming the Document Entry objects remained associated to the original version of the Folder object (did not get moved to new version of Folder).

The following validations are run on the query results:
 
* same_logicalId - both version of the Folder object have same logicalId 
* different_id - entryUUID attribute for each Folder object is different 
* orig_is_version_1 - the original version has version = 1 
* update_is_version_2 - the updated version has version = 2 
* orig_id_and_lid_same – the entryUUID and logicalId attributes have the same value for the original Folder object
* update_id_and_lid_different - the entryUUID and logicalId attributes are different for the updated Folder object 
* uniqueid_same - the uniqueId attribute on both Folder objects are the same 
* original_is_deprecated - the initial version of the Folder object has an availabilityStatus of Deprecated 
* update_is_approved - the update version of the Folder has an availabilityStatus of Approved 
* orig_folder_has_references – both Association objects remain referencing the original version of the Folder 
* update_folder_has_no_references – neither Association objects were propagated to the new version of the Folder (e.g.: Folder remains empty) 
