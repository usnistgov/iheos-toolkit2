SQ.b XDSResultNotSinglePatient Error

The Stored Query transaction is restricted from returning full metadata (LeafClass) for more than one Patient ID.  When ObjectRefs are 
requested, there is no restriction since PII is not being disclosed.

Each subtest deals with a combination of an object type 
(DocumentEntry, SubmissionSet, Folder) matched up with a return 
type (LeafClass, ObjectRef) to ensure the proper response is 
generated.

This test is dependent on both 12346 and 12374 since they load 
metadata into different Patient IDs.
