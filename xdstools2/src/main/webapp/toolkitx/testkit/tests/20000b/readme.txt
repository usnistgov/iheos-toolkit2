Folder - Reject Original Submission

Test ability of Registry to reject a Update Folder Metadata operation as defined in section 3.57.4.1.3.3.3 of the Metadata Update Supplement. For this test, the original version of the folder will be resubmitted using Metadata Update. The update should be rejected by the Document Registry. Original versions must be submitted to the Registry
with ITI-42 and not ITI-57.

There are two things that should be detected by the Registry and cause the rejection:

* Folder has a lid (logicalID) with value not equal to the entryUUID
* SS-to-FD HasMember Association has a PreviousVersion slot
