WADO Enhanced

<h2>WADO Retrieve, enhanced DICOM content</h2>

<h3>Purpose / Context</h3>
The Imaging Document Source has submitted a KOS for a study one or more images 
from a DICOM Enhanced SOP Class. In this test, we use some of the optional 
request parameters found in RAD TF 3:4.55.4.1.2 to exercise retrieves of single 
frames.
<h3>Prior to running this test:</h3>
<ol>
<li/>Imaging Document Source System under Test (IDS SUT): Import the imaging 
study with patient ID IDS-DEPT012-a.
<li/>IDS SUT: Generate one KOS object per the XDS-I profile that references the 
single image in this study. Submit that KOS object using a RAD-68 transaction to 
the Repository/Registry simulator configured for your Imaging Document Source 
(for example, acme__rep-reg). Use the following patient identifier with the 
RAD-68 submission:<br/>
IDS-AD012-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
<li/>IDS SUT: This test will use the WADO Retrieve (RAD-55) transaction to 
request several frames from the enhanced image as JPEG files.

