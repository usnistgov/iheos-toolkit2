PnR Single
<h2>Validate Key Object Selection Document, Single image study</h2>

Tests the ability of the Image Document Source actor (SUT) to send a Provide and
Register Imaging Document Set (RAD-68) transaction to a Repository/Registry
actor (Simulator), for a single DICOM image file.

<h3>Purpose / Context</h3>
The Imaging Document Source is required to submit a DICOM KOS object that 
references an imaging study with a single image. The test points are:<ul>
<li/>Imaging Document Source can execute a proper Provide and Register 
transaction per the XDS.b requirements.
<li/>Imaging Document Source includes metadata specified by the RAD-68 transaction.
<li/>Imaging Document Source includes metadata specified by this test as part of 
data requirements in a typical Affinity Domain.
<li/>KOS object is a legal KOS object per DICOM specifications.
<li/>KOS object contains<ul>
<li/>correct references to the single input image
<li/>correct demographics, patient identifiers, Study Instance UID
<li/>a Series Instance UID that is different from the Series Instance UID in the 
input image; the KOS can be in the same study as the original image but it must 
be created in a separate series per DICOM requirements.</ul</ul>

<h3>Prior to running this test:</h3>
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>Configure your Imaging Document Source System under Test (IDS SUT) to send
to the Repository/Registry simulator in the test environment.
<li/>Import the test image for the test data set IDS-DEPT001-a into your IDS SUT,
using whatever method is appropriate for your system.
<li/>Generate one KOS object per the XDS-I profile that references the single 
image in this study.
<li/>Submit that KOS object using a RAD-68 transaction to the 
Repository/Registry simulator configured for your IDS SUT (e.g.: acme__rep-reg). 
Use the following patient identifier with the RAD-68 submission:<br/>
IDS-AD001-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
</ol>
<p/><b>Note: </b> This test does not retrieve images using the RAD-16, RAD-55 or 
RAD-69 transactions. Other tests will use the same configuration/setup and will 
retrieve images using those transactions.

