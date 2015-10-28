MU - Simple DocumentEntry Update

Tests ability of Document Registry to accept a metadata update which changes a few simple
attributes of a DocumentEntry. This exercises the basic operation
of the Update DocumentEntry Metadata operaton as defined in section
3.57.4.1.3.3.1 of the Metadata Update Supplement

Sections:

Section: original - submit an original DocumentEntry, this is a first version.

Section: update - submit an update to the DocumentEntry, changing the creationTime

There are two documented "triggers" for this operation
1) The id attribute of the original is installed as the lid
attribute on the updated DocumentEntry.  This must be UUID format.
2) The SubmissionSet to DocumentEntry Association has a PreviousVersion
slot with a single value, 1, the version number of the DocumentEntry
being replaced.

Normally this would be discovered through query. But for testing
we simplify.

Formatting the updated DocumentEntry goes as follows:
- new id
- logicalId is id of original
- objectType must match original
- uniqueId must match original
- some attributes different (this is the point of the update)

Section: query_by_uniqueid - has two steps, uniqueid_query which
executes the GetDocuments query to fetch
both versions of the DocumentEntry.  This is done with $MetadataLevel not
present in the query, a non-Metadata Update Document Consumer.
A non-MetadataUpdate Document Consumer may not understand the relationship
between the two entries returned, but it will get two entries.  

This must be run after sections original and update

Section: query_by_uniqueid (step validate) - this runs a series of 
validations against the two DocumentEntries returned:

same_logicalId - both have same logicalId

different_id - entryUUID attribute on each is different

orig_is_version_1 - the original version has version = 1

update_is_version_2 - the updated version has version = 2

orig_id_and_lid_same - on original DocumentEntry, the id and lid attributes 
have the same value

update_id_and_lid_different - on the update DocumentEntry, the id and lid 
attributes have different values

uniqueid_same - the uniqueId attribute on both DocumentEntries is the same

original_is_deprecated - the initial version of the DocumentEntry must be
deprecated by the update

update_is_approved - the update of the DocumentEntry must have availabilityStatus
of Approved

Section: no_orig_docentry - an update is submitted but no original DocumentEntry
exists. The logicalId is hard coded to a unique value. This must fail.


Section: initial_version - submit a first version of a DocumentEntry via
update.  This is illegal, you cannot use metadata update to submit the first
version of a document. (ITI TF-2b:3.57.4.1.3.1 Rule #2)