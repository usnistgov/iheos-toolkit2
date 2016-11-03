Xfer Syntax JPEG Lossless, Single IDS

<h2>Transfer Syntax JPEG Lossless, Single IDS</h2>

<p/>Tests the ability of the Responding Imaging Gateway actor (SUT) to respond
correctly to a Cross Gateway Retrieve Imaging Document Set (RAD-75) transaction
from a Initiating Imaging Gateway actor (Simulator), for a single DICOM image 
file using the JPEG lossless (1.2.840.10008.1.2.4.70) Transfer Syntax. 

<h3>Prior to running this test:</h3>
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>If needed, click the "Test Context" box and select your Responding Imaging
Gateway actor as the System Under Test (SUT).
<li/>Configure your Responding Imaging Gateway System under Test (RIG SUT) to
recognize the three Image Document Source actor simulators (E, F, and G) in the
Generated Environment.
</ol>

<p/><b>Note:</b> Although the test environment provides for multiple Image 
Document Sources, this test expects a single image to be returned from a single 
Imaging Document Source (E).