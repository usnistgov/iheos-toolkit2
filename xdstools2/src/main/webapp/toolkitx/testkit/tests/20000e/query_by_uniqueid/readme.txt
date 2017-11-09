This test uses a GetFolderAndContents query setting the parameters, $XDSFolderEntryUUID, using the XDSFolder.entryUUID returned in the step, update_no_ap.  The parameter, $MetadataLevel is set equals to one (1) to confirm the Document Registry's ability to return results correctly for a Level One - Document Consumer.
 
Only the new version of the Folder object should be return by this query.
 
The following validations are run on the query results:

* update_id_and_lid_different- confirm the Folder's entryUUID and logicalId are different
* update_is_version_2 – confirm the Folder version equals two (2).
* update_is_approved – confirm the Folder has an availabilityStatus of Approved 
