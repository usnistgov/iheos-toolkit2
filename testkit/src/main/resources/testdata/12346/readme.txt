Initialize XDS.b for Registry Stored Query Testing

This test data is suitable for Stored Query testing only. It uses Register.b to load the Registry.
The Repository references in metadata are fake.

It must be submitted against a new Patient ID.  This can be done in two ways. If the entire
test is run the first section generates a new Patient ID and submits it via a
V2 Patient Identity Feed transaction to the Registry.

If your Registry does not accept the V2 Patient Identity Feed transaction then
do no run this first section and instead add the



Queries that
reference this data are sometimes graded by how many
objects they return.  If extra data is loaded in this Patient ID
then these counts can be invalidated.
