This step will test the ability of a Document Registry to report a failure when the a new version of the DocumentEntry is submitted with a status of Deprecated, and the transaction includes an UpdateAvailabilityStatus operation to set the DocumentEntry to Approved.

In ebXML 3.0, the Document Registry is responsible for maintaining the lifecycle of submissions.  If accepted, the new version of the DocumentEntry will be automatically marked as Approved.
  
Thus, updating the DocumentEntry availabilityStatus to Approved would not be logical and should fail.  Any failure that occurs during the transaction should result in the rollback of the entire transaction and an XDSMetadataUpdateError returned in the response message.
  
At the end of this test, only the original version of the DocumentEntry object should exist on the Document Registry and its availabilityStatus remain as Approved.
