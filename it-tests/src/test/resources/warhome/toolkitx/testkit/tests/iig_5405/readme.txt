Unknown DICOM UIDs, Single Gateway

<h2>Unknown DICOM UIDs, Single Responding Gateway</h2>

<p>Tests the ability of the Initiating Imaging Gateway actor (SUT) to respond
correctly to a Retrieve Image Document Set (RAD-69) Request from an Image Document 
Consumer actor (Simulator), for a single DICOM image file, in the case where
the Imaging Document Source has no image with matching DICOM UID values.
The DICOM UIDs in the RAD-69 request do not refer to an image that is known to the
Imaging Document Source (A1) behind the Responding Imaging Gateway (A),
and an error code will be returned by the Responding Imaging Gateway to the Initiating Imaging Gateway.
</p>


<h3>Retrieve Parameters</h3>
<table border="1">
 <tr><td>RIG Home Community ID (A)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td></tr>
 <tr><td>IDS Repository Unique ID (A1)</td><td>1.3.6.1.4.1.21367.13.71.101</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

<p/><b>Note:</b> Although the test environment provides for three Responding 
Imaging Gateways and multiple Image Document Sources, this test accesses a 
single Responding Imaging Gateway (A) and a single Imaging Document Source (A1) 
behind that gateway.