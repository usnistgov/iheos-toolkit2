R.b Reject Submission, Patient ID on Replacement Document does not match Original

This test requires special handling. Each of the sections must be run
separately using the Select Section: control below. You will need two
valid Patient IDs for your Document Registry. Then . . .

1) Run section "submit" using your first Patient ID.

2) Run section "rplc" using your second Patient ID. The results will
show that the Registry returned an error indicating a non-matching
Patient ID. This is an expected error.  It will be displayed in black
indicating that the test is still succeeding.

3) Run section "eval" using any Patient ID (doesn't use it).
This should succeed at detecting that the RPLC failed.

submit - submit single document

rplc - allocate new Patient ID and then issue a document replace.  Transaction must fail.

eval - Verify the new DocumentEntry does not exist, the old still has status Approved