Reject resubmission

A submission containing objects with pre-assigned UUIDs cannot be re-submitted.  The UUIDs must be unique.  Most Document Sources submit objects with symbolic IDs (missing a urn:uuid: prefix) and allow Registry to assign the permanent UUID.

This test:

1) Submits DocumentEntry with a new, unique UUID for its id attribute

2) Queries to verify submission accepted

3) Resubmits DocumentEntry with same UUID - verifies that it is rejected

4) Queries for the DocumentEntry, verifies that the first submission, not the second, returned.

