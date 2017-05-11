Multiple Imaging Document Source Actors (E, F)

<h2>Multiple Imaging Document Source Actors (E, F)</h2>

<p/>Tests the ability of the Responding Imaging Gateway actor (SUT) to respond
correctly to a Cross Gateway Retrieve Imaging Document Set (RAD-75) transaction
from a Initiating Imaging Gateway actor (Simulator) for DICOM image files from 
two Imaging Document Source actor (Simulators).

<p/>One study is located in Imaging Document Source E, and the second study is
located in Imaging Document Source F.
The Responding Imaging Gateway is expected to submit retrieve requests to both
Imaging Document Source actors and provide a consolidated result.


<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td></tr>
 <tr><td>IDS Repository Unique ID (E)</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>
 <tr><td>IDS Repository Unique ID (F)</td><td>1.3.6.1.4.1.21367.13.71.201.2</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>