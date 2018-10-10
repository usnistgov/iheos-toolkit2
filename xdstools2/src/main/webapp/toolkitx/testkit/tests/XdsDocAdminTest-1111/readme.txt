Document Administrator Update Document Set Tests

Init In the role of an Integrated Source Repository, Toolkit initializes the supporting simulator with a Document Entry. The 'confidentialityCode' is initially set to 'U' (Unrestricted).

MetadataUpdate The Document Administrator SUT performs an ITI-57 Update document Set, against the Document Entry submitted in the Init section, to update confidentialityCode to 'R' (Restricted).

Query In the role of a Document Consumer, Toolkit queries the Registry to validate confidentialityCode was updated successfully.
