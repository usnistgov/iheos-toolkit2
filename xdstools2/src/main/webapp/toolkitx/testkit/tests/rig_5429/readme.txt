Multiple Imaging Document Source Actors

<h2>Multiple Imaging Document Source Actors</h2>

<h3>Imaging Document Source: E,F</h3>
<h3>Purpose / Configuration</h3>
<p>A retrieve request is sent to the  Responding Imaging Gateway for two studies
in separate Imaging Document Sources using one transfer syntax value.</p>

<table border="1">
 <tr><td>RIG Home Community ID</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td><tr>
 <tr><td>IDS Repository Unique ID (E)</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>
 <tr><td>IDS Repository Unique ID (F)</td><td>1.3.6.1.4.1.21367.13.71.201.2</td></tr>
 <tr><td>Transfer Syntax UID</td><td>1.2.840.10008.1.2.1</td></tr>
</table>

<p>
One study is located in Imaging Document Source E, and the second study
is located in Imaging Document Source F. The Responding Imaging Gateway
is expected to submit retrieve requests to both Imaging Document Source
actors and provide a consolidated result. 
</p>
<p>
The test points are:
</p>
<ul>
 <li>The Responding Imaging Gateway is able to accept a RAD-75 transaction
     from an Initiating Imaging Gateway simulator, trigger RAD-69 transactions
     to the Imaging Document Source simulators (E and F), gather the results and
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
