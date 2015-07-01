R.b Reject Submission of Invalid Patient ID

Verify a Register.b transaction is rejected by your Registry given
that it carried an unknown Patient ID.  The correct error code
(XDSUnknownPatientId) must be returned.

Enter into the Patient ID box a Patient ID that is not registered
with your Document Registry.  Run the test.  A success indicates
that your Document Registry rejected the submission.