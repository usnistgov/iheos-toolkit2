Use the GetDocuments query to verify the submission of the updated DocumentEntry object. 
The following validations are run on the query results: 
* version - the updated version = 2 
* lid - the entryUUID and logicalId attributes are different for the updated DocumentEntry object
* approved - the updated version of the DocumentEntry object has an availabilityStatus of Approved 

