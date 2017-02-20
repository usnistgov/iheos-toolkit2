Retrieve with Persistance

This test starts by having the operator enter a Patient ID that is valid in their
On Demand Responding Gateway.  This patient must have a single On Demand Document.

Next a GetAll Stored Query is used to get the details about this 
On Demand Document.

Based on the metadata for the On Demand Document returned by the GetAll, a Retrieve
is sent for the On Demand Document. With the persistance option this should cause
a SnapShot to be generated.

A second GetAll is sent to gather the Snapshot Association and the DocumentEntry
for the persistant Document. These are verified.

The persisted Document is retrieved and compared to the results of the first retrieve.
