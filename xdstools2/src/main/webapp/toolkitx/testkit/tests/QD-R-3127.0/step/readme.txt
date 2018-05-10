The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000045 PID]</li>
<li>$XDSDocumentEntryStatus = Deprecated returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000045.2 OR ($XDSDocumentEntry.PatientID = [P-000000045 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000045.2])</li>
<li>A match on: $XDSDocumentEntryStatus = [value from D-000000045.2]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response.

The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000201 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryCreationTimeFrom =20090514141516 returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000201.10 OR (XDSDocumentEntry.patientID = [P-000000201 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000201.10])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000201.10]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000201.10]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000201.11 OR (XDSDocumentEntry.patientID = [P-000000201 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000201.11])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000201.11]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000201.11]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Creation Time should not be part of that check.
