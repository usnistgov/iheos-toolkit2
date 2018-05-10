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
