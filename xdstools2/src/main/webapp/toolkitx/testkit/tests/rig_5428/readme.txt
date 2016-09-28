One Multi-image Study, Single IDS

<h2>One Multi-image Study, Single IDS</h2>

<h3>Imaging Document Source: F</h3>
<h3>Purpose / Configuration</h3>
<p>A retrieve request is sent to the  Responding Imaging Gateway for a
single study with multiple images using one transfer syntax value.
</p>

<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td><tr>
 <tr><td>IDS Repository Unique ID (F)</td><td>1.3.6.1.4.1.21367.13.71.201.2</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

<p>
The Responding Imaging Gateway communicates with Imaging Document Source F
and returns one imaging study with multiple  images.
</p>

<p>
The test points are:
</p>
<ul>
 <li>The Responding Imaging Gateway is able to accept a RAD-75 transaction
     from an Initiating Imaging Gateway simulator, trigger a RAD-69 transaction
     to the Imaging Document Source simulator (F), gather the results and
     return a proper RAD-75 response to the Initiating Imaging Gateway simulator.</li>

 <li>The RAD-69 transaction includes all parameters, including the proper Repository Unique ID.</li>
 <li>The retrieved images are encoded with the proper transfer syntax and have proper identifiers (UIDs).</li>
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
