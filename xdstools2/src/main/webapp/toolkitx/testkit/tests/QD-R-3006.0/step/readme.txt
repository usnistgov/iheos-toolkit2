The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool)</li>
<li>$XDSDocumentEntryPatientID = [P-000000018 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryAuthorPerson = Michael Huntér (use the decomposed unicode code points 0065+0301 for the accented e)</li>
<li>returnType = leafClass</li>
<li>SOAP request = synchronous</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000018.10 OR (XDSDocumentEntry.patientID = [P-000000018 PID] AND XDSDocumentEntry.authorPerson = [value from D+000000018.10])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000018.10]</li>
<li>A match on: XDSDocumentEntry.authorPerson = [value from D-000000018.10]</li>
</ul>
NOTE: The parameters that are part of the Request should be the minimum that's checked on the
Response.