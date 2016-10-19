KOS Validation, single image
<h2>Validate Key Object Selection Document, Single image study</h2>

<p/>Prior to running this test:
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>Configure your Imaging Document Source System under Test (IDS SUT) to send
to the Repository/Registry simulator in the test environment.
<li/>Load the test image for the test data set IDCDEPT001 into your IDS SUT,
using whatever method is appropriate for your system.
<li/>Send a Provide and Register Imaging Document Set (RAD-68) transaction from
your IDS SUT to the Repository/Registry for this test image.
</ol>

<p/>When the test is run:
<p/>The test kit will retrieve information regarding the RAD-68 transaction
from the Repository/Registry Simulator, evaluate it, and report the results.

<p>The following validations are performed:
<ol>
<li/>Correct DICOM tag values in the KON.
</ol>
</p>