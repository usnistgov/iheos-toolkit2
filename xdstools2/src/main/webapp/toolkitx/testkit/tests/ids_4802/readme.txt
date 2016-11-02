PnR Multi
<h2>Validate Key Object Selection Document, Multi image study</h2>

Tests the ability of the Image Document Source actor (SUT) to send a Provide and
Register Imaging Document Set (RAD-68) transaction to a Repository/Registry
actor (Simulator), for multiple DICOM image files.

<h3>Prior to running this test:</h3>
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>Configure your Imaging Document Source System under Test (IDS SUT) to send
to the Repository/Registry simulator in the test environment.
<li/>Load the test image for the test data set IDS-DEPT002-a into your IDS SUT,
using whatever method is appropriate for your system.
<li/>Generate one KOS object per the XDS-I profile that references the images in 
this study.
<li/>Submit that KOS object using a RAD-68 transaction to the 
Repository/Registry simulator configured for your IDS SUT (e.g.: acme__rep-reg). 
Use the following patient identifier with the RAD-68 submission:<br/>
IDS-AD002-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
</ol>
<p/><b>Note: </b> This test does not retrieve images using the RAD-16, RAD-55 or 
RAD-69 transactions. Other tests will use the same configuration/setup and will 
retrieve images using those transactions.


