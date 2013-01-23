R.b Add Existing document to existing folder

This test validates a Registry's ability to perform a basic Folder
operation, adding a Document already in Registry to a Folder also 
already in Registry.

create_folder - creates a folder and a document to work with

add_to_folder - adds document to folder

eval - verify registry contents


FAQ regarding the add_to_folder section:

Q: I have a little problem with the XDS Registry test 12326. 
An existing document shall be added to an existing folder via a new submission set. 
In the technical framework it is described that when an existing document is referenced 
via a new submission set there must be an HasMember Association with the Association Label 
"ByReference" between the submission set and the document entry. But in this test only the 
association between folder and document and the association between submission set and 
the folder/document association is sent. 

Is this correct or is the test wrong?

A: In test 12326, the create_folder section submits:

   A SubmissionSet
   A DocumentEntry linked to the SubmissionSet via a HasMember Association with 
   SubmissionSetStatus of Original (new submission)
   A Folder linked to the SubmissionSet via a HasMember Association

So, at the end of this submission the Registry contains a new DocumentEntry and 
a new Folder.  There is no linkage between them (other than that they were submitted 
in the same SubmissionSet). The DocumentEntry and the Folder could have been 
submitted in separate submissions and this test would be the same.

In the second submission, section add_to_folder, the DocumentEntry is added 
to the Folder. So, the submission contains:

   The Folder to DocumentEntry HasMember Association (declaring the 
         DocumentEntry to be a member of the Folder)
   The HasMember Association linking the SubmissionSet to the above 
         Association.  This documents which submission added the 
         DocumentEntry to the Folder (who is responsible for this 
         DocumentEntry being in the is folder).

The SubmissionSetStatus attribute is only used when linking a 
DocumentEntry to a SubmissionSet.  No DocumentEntries were submitted 
or referenced by this submission.

So, going back to your original question, the ByReference label 
is used when adding a DocumentEntry to an existing SubmissionSet. 
This is occasionally done because documents of different Patient 
IDs need to be linked.  Mother and child during child birth is the 
typical example here.  The only way to link documents of different 
Patient IDs is through the ByReference label to a SubmissionSet. This 
is not the focus of this test.
