<h3>Send RAD-69 Request to IDS SUT - Comparison Transaction.</h3>

<p/>This section uses the same transaction as the other cases, but has no errors. 
If you are set up correctly, it should run successfully and return a single 
image. If this section fails, the results from subsequent sections in this test
may not be valid.

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) sends a Retrieve Image 
Document Set Request (RAD-69 Request) to the Imaging Document Source System
Under Test (IDS SUT).
<li/>The IDS SUT is expected to process the test and generate a response
containing the requested image.
<li/>The IDC sim will process the RAD-69 response and store the received
image.
<li/>No validation will be made on the SOAP Response or the received DICOM
object, other than that the transaction was successful.
</ol>