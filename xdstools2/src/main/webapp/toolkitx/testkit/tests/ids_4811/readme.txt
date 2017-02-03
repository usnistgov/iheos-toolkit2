SOAP Multi

<h2>Retrieve Image Document Set, Multiple Images</h2>

<p/>Tests the ability of the Image Document Source actor (SUT) to respond 
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image 
Document Consumer actor (Simulator) for multiple DICOM image files.

<p/>The Imaging Document Source has submitted a KOS for a study with a single image in test 4802.
If you have not completed the action of submitting the KOS object for the test images and making
those images available for retrieve, you need to complete that step now.

<p/>In this test, we use the RAD-69 transaction to retrieve the images referenced in the KOS.
This test uses the following patient identifier:
<blockquote>IDS-AD002-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&ISO</blockquote>

<ul>
 <li>Successfully complete IDS Test 4802.</li>
 <li>Make sure the images referenced in IDS Test 4802 are ready for retrieve using the RAD-69 transaction.</li>
 <li>Execute this test to perform a retrieve and validation.
</ul>
