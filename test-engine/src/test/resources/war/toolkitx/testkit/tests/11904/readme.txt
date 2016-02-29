SQ.b GetDocumentsAndAssociations Stored Query

Test 12346 must be run first to load test data into the Registry.

Test Steps

uniqueid
	Query with uniqueId.  DocumentEntry targeted was submitted alone in submissionset so
	only the DocumentEntry and a single Association should be returned.

uniqueids
	Query with two UniqueIds.  One of the targeted DocumentEntries is the same as
	above.  The second is a member of a Folder. Two DocumentEntries are expected
	back and 3 Associations: SubmissionSet to DocumentEntry association for each
	DocumentEntry AND a single Folder to DocumentEntry association for the second
	DocumentEntry.

uuid
	Operation with UUID. This is a repeat of step uniqueid above but starting with the
	UUID of the DocumentEntry instead of the uniqueId.

uuids
	Operation with two UUIDs.  This is a repeat of step uniqueids above but starting
	with the UUIDs of the DocumentEntries instead of the uniqueIds.
