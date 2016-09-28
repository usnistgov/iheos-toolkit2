Unknown Imaging Document Source, Single IDS

<h2>Unknown Imaging Document Source, Single IDS</h2>

<h3>Purpose / Configuration</h3>
<p>A retrieve request is sent to the Responding Imaging Gateway for a
single imaging study with one image.</p>
<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td><tr>
 <tr><td>IDS Repository Unique ID (Unknown)</td><td>1.3.6.1.4.1.21367.13.71.999</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

<p>
The respositoryUniqueId in the RAD-75 request does not represent an
Imaging Document Source that is known to the Responding Imaging Gateway.
</p>
<p>
The test points are:
</p>
<ul>
 <li>The Responding Imaging Gateway is able to accept a RAD-75 transaction
     from an Initiating Imaging Gateway simulator, recognize that the
     Imaging Document Source referenced in the request is unknown, and return
     a proper RAD-75 error response to the Initiating Imaging Gateway simulator.</li>
 <li>The RAD-69 transaction includes all parameters, including the proper Repository Unique ID.</li>
 <li>No images are returned in the RAD-75 response</li>
</ul>


<h3>Test Steps</h3>
<p>
<ol>
<li/>one
<li/>two
</ol>
</p>

<h2>Validate SOAP Response and returned DICOM Image files</h2>

<p>
The following validations are performed:
<ol>
<li>Returned image matches that requested.
<li>Two
<li>three
</ol>
</p>
