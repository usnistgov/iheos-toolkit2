Xfer Syntax Failure, Single Gateway

<h2>Transfer Syntax Failure, Single Gateway</h2>

<p>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator), for a single DICOM image file using the JPEG 
Baseline (Process 1) (1.2.840.10008.1.2.4.50) Transfer Syntax, in the case where
the Image Document Source is not able to return the image file in that Syntax.
</p>

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
Imaging Gateways and multiple Image Document Sources, this test accesses a 
single Responding Imaging Gateway (A) and a single Imaging Document Source (A1) 
behind that gateway.