<h3>Send Retrieve Image Document Set (RAD-69) Request to IIG SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) will send a Retrieve Image 
Document Set Request (RAD-69 Request) to the Initiating Imaging Gateway System
Under Test (IIG SUT), requesting a single DICOM object file from both Responding 
Imaging Gateway A and B. The file requested from Gateway B is unknown. The SOAP 
Body for this request may be viewed in the "Metadata".
<li/>The IIG SUT is expected to process the test and generate appropriate Cross 
Gateway Retrieve Imaging Document Set (RAD-75) transactions to both Responding 
Imaging Gateway simulators.
<li/>Each RIG simulator will process its RAD-75 Request, generating a 
corresponding RAD-69 request to the appropriate Imaging Document Source 
simulator.
<li/>The IDS A1 simulator will process the RAD-69 request, generating a response
containing the requested image.
<li/>The IDS B1 simulator will process the RAD-69 request, generating a response
indicating that the requested image could not be found.
<li/>The RIG simulators will process the RAD-69 responses, generating RAD-75
responses, which will be returned to the IIG SUT.
<li/>The IIG SUT is expected to process the RAD-75 responses, generating
a single RAD-69 response and returning it to the IDC sim.
<li/>The IDC sim will process the RAD-69 response and store the received
image. Validation of test results is handled by subsequent sections of this 
test.
</ol>
