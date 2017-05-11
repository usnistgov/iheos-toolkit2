<h3>Send Retrieve Image Document Set (RAD-69) Request to RIG SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Initiating Imaging Gateway simulator (IIG sim) sends a Cross Gateway 
Retrieve Image Document Set Request (RAD-75 Request) to the Responding Imaging 
Gateway system under test (RIG SUT), requesting a single DICOM object file using 
an unknown DocumentUniqueId value. The SOAP Body for this request may be viewed 
in the "Metadata".
<li/>The RIG SUT is expected to process the RAD-75 Request, generating a 
corresponding RAD-69 request to the Imaging Document Source E simulator
(IDS E sim).
<li/>The IDS E sim will process the RAD-69 request, generating a response
indicating that it is not able to locate the requested image. 
<li/>The RIG SUT is expected to process the RAD-69 response, generating
a RAD-75 response and returning it to the IIG sim.
<li/>The IIG sim will process the RAD-69 response.
</ol>
