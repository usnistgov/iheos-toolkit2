MU - Update DocumentEntry Status

The primary reference for this test is section ITI TF-2b:3.57.4.1.3.3.2
Update DocumentEntry AvailabilityStatus.

Sections:

Section: load_docentry_1 - Submit a single DocumentEntry.  Of course it 
will be assigned availabilityStatus of Approved.

Section: deprecate_docentry_1 - Submit an update to deprecate docentry_1. 
This will consist of a SubmissionSet and a UpdateAvailabilityStatus
Association referencing docentry_1 

Section: confirm_docentry_1_deprecated - use GetDocuments Stored Query to 
retrieve metadata for docentry_1 and verify it has availabilityStatus
of Deprecated.

Section: undeprecate_docentry_1 - Submit an update to set the 
availabilityStatus on docentry_1 back to Approved.

Section: confirm_docentry_1_undeprecated - use GetDocuments Stored Query to 
retrieve metadata for docentry_1 and verify it has availabilityStatus
of Approved.

Section: load_docentry_2 - Submit a single DocumentEntry

Section: update_docentry_2 - Submit an update to docentry_2

Section: approve_orig_docentry_2 - Attempt to set the availabilityStatus
of the original version of docentry_2 back to Approved.  Since it is 
not the most recent version, this must fail.

Section: verify_orig_docentry_2_deprecated - Verify the previous step
did not change the availabilityStatus of the original version of
docentry_2 back to Approved.