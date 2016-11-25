Partial Success, Single IDS

<h2>Partial Success, Single IDS</h2>

<p/>Tests the ability of the Responding Imaging Gateway actor (SUT) to respond
correctly to a Cross Gateway Retrieve Imaging Document Set (RAD-75) transaction
from an Initiating Imaging Gateway actor (Simulator) for two DICOM image files,
in the case where one of the files is returned and the other is unknown to the 
Image Document Source.

<p/>The first imaging study is present in Imaging Document Source E.
The second imaging study is requested of Imaging Document Source E but is not present.


<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td></tr>
 <tr><td>IDS Repository Unique ID (E)</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>