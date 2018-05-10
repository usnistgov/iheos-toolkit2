The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000020 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryFormatCode = [code]^^[scheme] format code = urn:ihe:pcc:edr:2007
format code scheme = 2.16.840.1.113883.3.88.12.80.73</li>
<li>returnType = LeafClass SOAP</li>
<li>request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000020.1 OR (XDSDocumentEntry.patientID = [P-000000020 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000020.1])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000020.1]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000020.1]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000020.4 OR (XDSDocumentEntry.patientID = [P-000000020 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000020.4])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000020.4]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000020.4]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000020.5 OR (XDSDocumentEntry.patientID = [P-000000020 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000020.5])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000020.5]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000020.5]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response.
