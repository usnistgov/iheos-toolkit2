Partial Success, Single IDS

<h2>Partial Success, Single IDST</h2>

<p/>Tests the ability of the Responding Imaging Gateway actor (SUT) to respond
correctly to a Cross Gateway Retrieve Imaging Document Set (RAD-75) transaction
from a Initiating Imaging Gateway actor (Simulator) for two DICOM image files, 
in the case where one of the files is returned and the other is unknown to the 
Image Document Source. 

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