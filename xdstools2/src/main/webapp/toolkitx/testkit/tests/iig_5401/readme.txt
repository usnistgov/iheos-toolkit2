Two Imaging Studies, Single Gateway

<h2>Two Imaging Studies, Single Responding Gateway</h2>

<p>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator) for two DICOM image files from different studies.
</p>

<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID (A)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td></tr>
 <tr><td>IDS Repository Unique ID (A1)</td><td>1.3.6.1.4.1.21367.13.71.101</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

<p/><b>Note:</b> Although the test environment provides for three Responding 
Imaging Gateways and multiple Image Document Sources, this test expects a single
image to be returned, from a single Responding Imaging Gateway (A) and a single 
Imaging Document Source (A1) behind that gateway.
