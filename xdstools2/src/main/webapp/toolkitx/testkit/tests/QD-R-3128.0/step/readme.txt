The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000202 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryCreationTimeTo =20090514111111 returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000202.1 OR (XDSDocumentEntry.patientID = [P-000000202 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000202.1])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000202.1]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000202.1] ***without any child Slot, Classification, or ExternalIdentifier elements</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000202.19 OR (XDSDocumentEntry.patientID = [P-000000202 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000202.19])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000202.19]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000202.19] ***without any child Slot, Classification, or ExternalIdentifer elements</li>
</ul>
NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Creation Time should not be part of that check.
