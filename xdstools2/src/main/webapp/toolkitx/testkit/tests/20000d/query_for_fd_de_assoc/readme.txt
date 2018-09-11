This uses a GetFolderAndContents query setting the parameters, $XDSFolderEntryUUID, using the XDSFolder.entryUUID returned in the previous step and $MetadataLevel equals one (1).
  
The goal is to cross-reference that the Document Registry generate two FD-DE HM Associations for the new version of the Folder and these referenced the Document Entry objects originally created.

The following validation are run on the query results:

* found_targetObject01 - the Association object @targetObject references the one of the Document Entry objects returned in the previous step
* found_targetObject02 - the Association object @targetObject references the other the Document Entry object returned in the previous step

