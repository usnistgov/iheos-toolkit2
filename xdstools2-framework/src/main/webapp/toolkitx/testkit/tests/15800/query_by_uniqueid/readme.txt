

This uses a GetDocuments query to fetch the DocumentEntries by the common uniqueId.  It will return two DocumentEntries (both versions).

Validation are run on the query results:
* same_logicalId - both have same logicalId
* different_id - entryUUID attribute on each is different
* orig_is_version_1 - the original version has version = 1
* update_is_version_2 - the updated version has version = 2
* orig_id_and_lid_same - on original DocumentEntry, the id and lid attributes 
have the same value
* update_id_and_lid_different - on the update DocumentEntry, the id and lid 
attributes have different values
* uniqueid_same - the uniqueId attribute on both DocumentEntries is the same
* original_is_deprecated - the initial version of the DocumentEntry must be
deprecated by the update
* update_is_approved - the update of the DocumentEntry must have availabilityStatus
of Approved

