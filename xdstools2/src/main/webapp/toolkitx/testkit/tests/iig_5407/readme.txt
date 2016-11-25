Partial Success, Single Gateway

<h2>Partial Success, Single Responding Gateway</h2>

<p>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator), for two DICOM image files, in the case where one of
the files is returned and the other is unknown to the Image Document Source.
The first imaging study is present in Community A.
The second imaging study is requested of Community A but is not present.
</p>

<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID (A)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td></tr>
 <tr><td>IDS Repository Unique ID (A1)</td><td>1.3.6.1.4.1.21367.13.71.101</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

