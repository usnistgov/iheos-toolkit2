
Submit an update to the previously submitted DocumentEntry changing only the creationTime.

The DocumentEntry being targeted for update starts out having only a single version (version = 1). The new version submitted in this section will be assigned version = 2.  Both version 1 and version 2 will carry the same Logical ID (LID) indicating they are two versions of the same Logical Object.  They will have different IDs because they are different versions (and two objects can have the same ID).  Version 1 will have the same value for LID and ID.  Version 2 will have different values for ID and LID. 

In the update submission there are two "triggers" for this operation:
* The SubmissionSet to DocumentEntry HasMember Association has a PreviousVersion slot with a single value, 1, the version number of the DocumentEntry being replaced.
* The LID the original DocumentEntry is installed as the LID on the updated DocumentEntry.  This is in UUID format.

The update will be assigned version = 2.  This assignment is made in the Registry (not part of the submission).

Normally this would be discovered through query. But for testing
we simplify.

To review what goes into the updated DocumentEntry:
* new id
* LID is ID of original
* objectType must match original
* uniqueId must match original
* some attributes different (this is the point of the update)
