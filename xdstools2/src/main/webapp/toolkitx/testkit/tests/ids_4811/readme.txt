SOAP Multi

<h2>Retrieve Image Document Set, Multiple Images</h2>

<p/>Tests the ability of the Image Document Source actor (SUT) to respond 
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image 
Document Consumer actor (Simulator), for multiple DICOM image files.

<p/>The Imaging Document Source has submitted a KOS for a study with a single image in test 4811.
If you have not completed the action of submitting the KOS object for the test images and making
those images available for retrieve, you need to complete that step now.

<p/>In this test, we use the RAD-69 transaction to retrieve the single image referenced in the KOS.
This test uses the following patient identifier:
<blockquote>IDS-AD002-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&ISO</blockquote>

<h3>Prior to running this test:</h3>
<ol>
<li/>Create/select a test session.
<li/>Click the "Initialize Test Environment" button to create a test environment
for the test session.
<li/>Configure your Imaging Document Source System under Test (IDS SUT) 
Repository Unique ID to 1.3.6.1.4.1.21367.13.80.110.
<li/>Load the test images for the test data set xca-dataset-a1 into your IDS SUT,
using whatever method is appropriate for your system.
</ol>
