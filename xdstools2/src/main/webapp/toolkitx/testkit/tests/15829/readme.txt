Repeat retrieve

This test starts by having the operator enter a Patient ID that is valid in their
On Demand Responding Gateway.  This patient must have a single On Demand Document.

Next a Find Documents Stored Query is used to get the details about this 
On Demand Document.

Two Retrieves are issued to the Responding Gateway. The
content retrieved each time must be different. Between the two retrieves, 
the operator is given the
opportunity to trigger a content update on the 
Responding Gateway (System Under Test). The documents are judged as different if
their hashes are different.

This test does not require the Persistance Option.
