MHD Document Recipient/Responder Lifecyle (text/plain)

<p>The MHD Document Recipient may
implement the XDS ON FHIR option and require an XDS Document Repository/Registry pair behind it or may operate without
the option. This test setup does not provide the Repository/Registry pair automatically but Toolkit simulators may be
created and configured separately. This test will split into several in the future to handle different configurations.

<p>This test suite can work with two URLs.  The normal FHIR base address is for query and read operations.  A separate
address can be configured for MHD transations if needed. For this test to run both FHIR and PDB addresses must be configured
in your system definition.

<p>The metadata supplied in the Document Reference and Document Manifest resources exceed the minimum requirements.  The
Document Reference returned from the query is not checked for content so systems that do not implement the
Comprehensive Metadata option can still use this test.

<p>The expected response from the READ Document operations assume that some optional behaviour is present.
Specifically that if the Accept HTTP header is a FHIR type then the Binary resource is returned and if the
Accept header is the raw document
type (text/plain for example) then only the document contents will be returned. These tests will be adjusted to be more
 accurate in the future.


