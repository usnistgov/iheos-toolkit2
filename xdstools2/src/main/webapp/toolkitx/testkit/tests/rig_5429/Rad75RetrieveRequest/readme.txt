<h3>Send Retrieve Image Document Set (RAD-69) Request to RIG SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Initiating Imaging Gateway simulator (IIG sim) sends a Cross Gateway 
Retrieve Image Document Set Request (RAD-75 Request) to the Responding Imaging 
Gateway system under test (RIG SUT), requesting a DICOM image files from both
IDS E and F. The SOAP Body for this request may be viewed in the "Metadata".
<li/>The RIG SUT is expected to process the RAD-75 Request, generating  
appropriate RAD-69 requests to the Imaging Document Source E and F simulators.
<li/>Each IDS sim will process its RAD-69 request, generating a response
containing the requested images.
<li/>The RIG SUT is expected to process the RAD-69 responses, generating
a consolidated RAD-75 response and returning it to the IIG sim.
<li/>The IIG sim will process the RAD-69 response and store the received
images.
</ol>
