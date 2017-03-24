Transfer Syntax Failure, Single IDS

<h2>Transfer Syntax Failure, Single IDS</h2>

<p/>Tests the ability of the Responding Imaging Gateway actor (SUT) to respond
correctly to a Cross Gateway Retrieve Imaging Document Set (RAD-75) transaction
from an Initiating Imaging Gateway actor (Simulator), for a single DICOM image
file using the JPEG Baseline (Process 1) (1.2.840.10008.1.2.4.50) Transfer 
Syntax, in the case where the Image Document Source is not able to return the 
image file in that Syntax.

<p/>The image is not available on the Imaging Document Source with the requested
transfer syntax, and an error code will be returned by the Imaging Document Source
to the Responding Imaging Gateway.


<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td></tr>
 <tr><td>IDS Repository Unique ID (E)</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.4.50: JPEG Baseline (Process 1)</td></tr>
</table>