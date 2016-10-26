Multi-Image Study, Single Gateway

<h2>Multi-Image Study, Single Responding Gateway</h2>

<p/>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator), for multiple DICOM image files from a single study.

<h3>Prior to running this test:</h3>
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>If needed, click the "Test Context" box and select your Initiating Imaging
Gateway actor as the System Under Test (SUT).
<li/>Configure your Initiating Imaging Gateway System under Test (IIG SUT) to
recognize the three Responding Imaging Gateway simulators (A, B, and C) in the
Generated Environment.
</ol>

<p/><b>Note:</b> Although the test environment provides for three Responding 
Imaging Gateways and multiple Image Document Sources, this test expects 
images to be returned, from a single Responding Imaging Gateway (B) and a single 
Imaging Document Source (B1) behind that gateway.