Use the stored query, GetRelatedDocuments, to verify that both the SubmitAssociation and UpdateAvailabilityStatus operations were  successful in the previous step. 
This query is done twice changing the query parameter, $MetadataLevel: 
* Level 1 - The Append Association and linked DocumentEntry objects are not returned.
* Level 2 – Both DocumentEntry objects and the Append Association are returned. The Append Association will have an availabilityStatus of Deprecated.
The following validations are also run on the Level Two query results:
* patientIdCheck01 - confirm the patient identifier for the first DocumentEntry object is the same used in the original submission. 
* patientIdCheck02 – confirm the patient identifier for the second DocumentEntry object is the same used in the original submission. 
* associationCheckSource – confirm the entryUUID submitted for the first DocumentEntry object is correct. 
* associationCheckTarget – confirm the entryUUID submitted for the second DocumentEntry object is correct. 
* associationCheckType – confirm the object returned is an Append Relationship Association.
* associationCheckStatus - confirm the Association object returned has an availabilityStatus of Deprecated.
