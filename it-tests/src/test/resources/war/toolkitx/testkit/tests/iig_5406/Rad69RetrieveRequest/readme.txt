<h3>Send Retrieve Image Document Set (RAD-69) Request to IIG SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) will send a Retrieve Image 
Document Set Request (RAD-69 Request) to the Initiating Imaging Gateway System
Under Test (IIG SUT), requesting a single DICOM object file using an unknown
HomeCommunityId value. The SOAP Body for this request may be viewed in the 
"Metadata".
<li/>The IIG SUT is expected to process the request and generate a Retrieve 
Imaging Document Set (RAD-69) response indicating the error and returning it to 
the IDC sim.
<li/>The IDC sim will process the RAD-69 response. Validation of test results is 
handled by subsequent sections of this test.
</ol>
