The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000017 PID]</li>
<li>$XDSDocumentEntryStatus = Approved OR Deprecated</li>
<li>$XDSDocumentEntryEventCodeList = [code]^^[scheme] event code list item = T-32000
event code item scheme = SNM3 returnType = LeafClass</li>
<li>SOAP request = synchronous returnComposedObjects = true</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000017.1 OR (XDSDocumentEntry.patientID = [P-000000017 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000017.1])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000017.1]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000017.1]</li>
<ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000017.4 OR (XDSDocumentEntry.patientID = [P-000000017 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000017.4])</li>
<li>A match on: $XDSDocumentEntryStatus = [value from D-000000017.4]</li>
<li>A match on: $XDSDocumentEntryEventCodeList = [value from D-000000017.4]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000017.12 OR (XDSDocumentEntry.patientID = [P-000000017 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000017.12])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000017.12]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000017.12]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000017.13 OR (XDSDocumentEntry.patientID = [P-000000017 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000017.13])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000017.13]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000017.13]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response.

