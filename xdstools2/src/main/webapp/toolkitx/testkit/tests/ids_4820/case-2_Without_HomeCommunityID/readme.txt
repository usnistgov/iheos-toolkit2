<h3>Send Retrieve Image Document Set (RAD-69) Request without HomeCommunityID to 
IDS SUT.</h3>

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) sends a Retrieve Image 
Document Set Request (RAD-69 Request) to the Imaging Document Source System
Under Test (IDS SUT). This message will not contain a HomeCommunityID Element and
value in the DocumentRequest.
<li/>The IDS SUT is expected to process the test and generate a response
containing the requested image, not returning the HomeCommunityId Element and
value in the DocumentResponse.
<li/>The IDC sim will process the RAD-69 response and store the received
image.
</ol>