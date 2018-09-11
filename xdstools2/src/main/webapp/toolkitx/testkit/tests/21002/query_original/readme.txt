Use the GetDocuments query to verify the submission of the original DocumentEntry. 
The following validations are run on the query results: 
* version - the original version has version = 1 
* lid - the entryUUID and logicalId attributes have the same value for the original DocumentEntry object
* status - the original version of the DocumentEntry object has an availabilityStatus of Deprecated 

