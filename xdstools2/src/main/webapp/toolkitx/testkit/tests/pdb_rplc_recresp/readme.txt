MHD Document Recipient/Responder Document Replace (text/plain)

<p>This test suite can work with two URLs.  The normal FHIR base address is for query and read operations.  A separate
address can be configured for MHD transactions if needed. For this test to run both FHIR and PDB addresses must be configured
in your system definition.

<p>The metadata supplied in the Document Reference and Document Manifest resources exceed the minimum requirements.  The
Document Reference returned from the query is not checked for content so systems that do not implement the
Comprehensive Metadata option can still use this test.

<p>This test generates three transactions.  First is a DocumentReference submission.  Second is a document replace.  Third is a READ of the first DocumentReference to verify that its status has been updated to superseded (the equivalent to deprecated in XDS).

