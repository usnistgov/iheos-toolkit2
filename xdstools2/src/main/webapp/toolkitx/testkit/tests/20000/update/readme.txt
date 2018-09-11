Folder - Submit Update

The Folder being targeted for update starts out having the original version (version = 1). The new version submitted in this section is assigned version = 2.  Both version 1 and version 2 will carry the same logicalId (@lid) indicating there are two versions of the same logical object, but have different entryUUID (@id), one for each version.
 
In the update submission there are two "triggers" for this operation:
* The SubmissionSet to Folder HasMember Association includes a PreviousVersion slot with the version number set to the version number of the Folder being replaced.
* The logicalId is set on the new version of the Folder to match the original version.
 
The updated version will be assigned version = 2. This assignment is made in the Registry (not part of the submission).
 
To review what goes into the new version of the Folder:
* new entryUuid (@id)
* the logicalId (@lid) is the entryUUID (@id) of original version
* objectType must match original 
* uniqueId must match original 
* change an attribute (For this test, the FolderCodeList classification.)  

