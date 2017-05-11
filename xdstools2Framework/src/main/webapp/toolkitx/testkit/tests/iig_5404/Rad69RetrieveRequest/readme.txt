<h3>Send Retrieve Image Document Set (RAD-69) Request to IIG SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) will send a Retrieve Image 
Document Set Request (RAD-69 Request) to the Initiating Imaging Gateway System
Under Test (IIG SUT), requesting a single DICOM object file in JPEG Baseline 
(Process 1) (1.2.840.10008.1.2.4.50) Transfer Syntax. The SOAP Body for this 
request may be viewed in the "Metadata".
<li/>The IIG SUT is expected to process the test and generate a Cross Gateway
Retrieve Imaging Document Set (RAD-75) transaction to Responding Imaging Gateway 
A simulator (RIG A sim).
<li/>The RIG A sim will process the RAD-75 Request, generating a 
corresponding RAD-69 request to the Imaging Document Source A1 simulator
(IDS A1 sim).
<li/>The IDS A1 sim will process the RAD-69 request, generating a response
indicating that it is not able to return the image in the requested Transfer
Syntax..
<li/>The RIG A sim will process the RAD-69 response, generating a RAD-75
response, which will be returned to the IIG SUT.
<li/>The IIG SUT is expected to process the RAD-75 response, generating
a RAD-69 response and returning it to the IDC sim.
<li/>The IDC sim will process the RAD-69 response. Validation of test results is 
handled by subsequent sections of this test.
</ol>
