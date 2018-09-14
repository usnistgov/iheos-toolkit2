Submit a new Append Association object linking both of the previously submitted DocumentEntry objects, and an UpdateAvailabilityStatus Association to set the availabilityStatus of the new Append Association object to Deprecated.  Both operations will be contained in a single transaction.

In ebXML 3.0, the Document Registry is responsible for maintaining the lifecycle of submissions. If accepted, the new Append Association object will be automatically marked as Approved.  The Document Registry is required next to apply the UpdateAvailabilityStatus operation on the new Association object following its installation.
 
At the conclusion of the transaction, the expected result is both the Association object will have  an availabilityStatus of Deprecated.

