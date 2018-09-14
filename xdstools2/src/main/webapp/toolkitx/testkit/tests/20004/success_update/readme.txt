This step will test the ability of a Document Registry to successfully process a new version of a Folder, and an UpdateAvailabilityStatus operation setting this new version of the Folder object to Deprecated.  Both operations will be contained in a single transaction.

In ebXML 3.0, the Document Registry is responsible for maintaining the lifecycle of submissions. If accepted, the new version of the Folder will be automatically marked as Approved and the previous version marked as Deprecated.  The Document Registry is required to apply the UpdateAvailabilityStatus operation on the new version of the Folder following its installation.
 
At the conclusion of the transaction, the expected result is both the new version and original version of the Folder has an availabilityStatus of Deprecated. 
