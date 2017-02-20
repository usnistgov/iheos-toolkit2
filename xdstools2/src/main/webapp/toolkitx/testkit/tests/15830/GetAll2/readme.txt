
Issue a GetAll Stored Query for the Patient ID entered.  

Based on the metadata for the On Demand Document returned by the GetAll, a Retrieve
is sent for the On Demand Document. With the persistance option this should cause
a SnapShot to be generated.

Verify the Snapshot by finding the IsSnapshotOf Association and verifying that 

1. Both DocumentEntries it references are in the GetAll results.
2. The Stable DocumentEntry is pointed to by the sourceObject attribute and the On
Demand DocumentEntry is pointed to by the targetObject attribute.
