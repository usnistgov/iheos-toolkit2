MHD Document Recipient/Responder Lifecyle (text/plain)

<p>The MHD Document Recipient may
implement the XDS ON FHIR option and require an XDS Document Repository/Registry pair behind it or may operate without
the option. This test setup does not provide the Repository/Registry pair automatically but Toolkit simulators may be
created and configured separately.

<p>This test suite can work with two URLs.  The normal FHIR base address is for query and read operations.  A separate
address can be configured for MHD transations if needed. For this test to run both FHIR and PDB addresses must be configured
in your system definition.

<p>The following operations are coded in these sections:
<li><b>submit</b> - send a Provide Document Bundle transaction with a single Document Reference (a text/plain file)
<li><b>read_document_reference</b> - send a Find Document References query to the FHIR Base address.  A single Document
Reference resource is expected back
<li><b>read_document_binary</b> - send a FHIR READ for the Binary resource submitted in the submit section.  The requested
mime type is application/fhir+json so the Binary resource is expected back.
<li><b>read_document_contents</b> - send a FHIR READ for the Binary resource submitted in the submit section.  The
requested mime type is text/plain so the PDF content without the Binary resource wrapper is expected back.

<p>The metadata supplied in the Document Reference and Document Manifest resources exceed the minimum requirements.  The
Document Reference returned from the query is not checked for content so systems that do not implement the
Comprehensive Metadata option can still use this test.


