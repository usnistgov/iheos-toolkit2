

This submission contains two fixed Patient IDs. SubmissionSet.patientId comes from test 15817 and DocumentEntry.patientId comes from test 15817b. Both of these Patient IDs
have been sent to the Registry in a Patient Identity Feed. With this inconsistency the Register transaction must return the XDSPatientIdDoesNotMatch error.

