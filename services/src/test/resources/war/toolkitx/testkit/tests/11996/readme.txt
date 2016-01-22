R.b Reject Submission of Invalid Patient ID

Verify a Register.b transaction is rejected by your Registry given
that it carried an unknown Patient ID.  The correct error code
(XDSUnknownPatientId) must be returned.

This test ignores the Patient ID set in the user interface and instead
generates a new value guaranteed to be unique.

A success indicates that your Document Registry rejected the submission.