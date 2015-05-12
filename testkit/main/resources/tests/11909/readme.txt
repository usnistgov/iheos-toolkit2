SQ.b GetRelatedDocuments Stored Query

Stored Query must use SOAP version 1.2

Relies on test data loaded by test 12346


Test Steps

no_initial_doc
	Original document does not exist. Nothing returned.

uniqueid_no_related
	Operation with single UniqueId as input but no related documents

uuid_no_related
	Operation with single UUID as input but no related documents

uniqueid
	Operation with single UniqueId as input.

near_folder
	Operation near a folder.  Folders and Submission Sets are not returnd.

uuid
	Operation with UUID input

