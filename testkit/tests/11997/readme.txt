R.b Reject Submission Set, Patient ID does not match Document

This submission contains two fixed Patient IDs. The SubmissionSet has
PatientID of 54321^^^&1.3.6.1.4.1.21367.2005.3.7&ISO and the DocumentEntry
has PatientID of 12345^^^&1.3.6.1.4.1.21367.2005.3.7&ISO.

First, both of these PatientIds must be registered with your Document
Registry so they are accepted. Then when the Register transaction
is received the XDSPatientIdDoesNotMatch error must be thrown.
If these Patient Ids are not registered with your Registry then the 
unknown Patient ID error may be thrown which is not the condition
being tested here.

submit - Send a Register.b transaction to your Registry where the 
Patient ID on the Submission Set and Document do not match.  Verify the
correct error code (XDSPatientIdDoesNotMatch) is returned.

eval - Verify submission did not update registry.