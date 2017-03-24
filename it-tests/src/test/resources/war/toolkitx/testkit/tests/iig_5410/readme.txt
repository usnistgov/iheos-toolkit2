Partial Success, Multiple Gateways

<h2>Partial Success, Multiple Gateways</h2>

<p>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator), for two DICOM image files from difference Responding 
Imaging Gateways, in the case where the file requested from one Gateway is 
unknown.</p>

<p>One study is located in Community A. The retrieve for the second study is defined
for Community B, but the imaging study is not present in Community B.
The Initiating Imaging Gateway is expected to submit retrieve requests to both
communities and provide a consolidated result. The consolidated result consists of a
PartialSuccess status and the images from the study in Community A.
</p>

<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID (A)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td></tr>
 <tr><td>IDS Repository Unique ID (A1)</td><td>1.3.6.1.4.1.21367.13.71.101</td></tr>
 <tr><td>RIG Home Community ID (B)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.102</td></tr>
 <tr><td>IDS Repository Unique ID (B1)</td><td>1.3.6.1.4.1.21367.13.71.102</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>