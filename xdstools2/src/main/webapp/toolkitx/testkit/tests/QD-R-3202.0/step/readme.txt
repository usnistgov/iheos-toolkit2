The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
</li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
</li>$XDSDocumentEntryPatientID = [P-000000008 PID]</li>
</li>$XDSDocumentEntryStatus=Approved</li>
</li>$XDSDocumentEntryServiceStartTimeFrom = 20070316 returnType = LeafClass</li>
</li>SOAP request = synchronous returnComposedObjects = true</li>
</ul>

</b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
</ul>
</li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000008.6 OR (XDSDocumentEntry.patientID = [P-000000008 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000008.6])</li>
</li>A match on: XDSDocumentEntry.status = [value from D-000000008.6]</li>
</li>A match on: XDSDocumentEntry.serviceStart = [value from D-00000008.6]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000008.8 OR (XDSDocumentEntry.patientID = [P-000000008 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000008.8])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000008.8]</li>
<li>A match on: XDSDocumentEntry.serviceStart = [value from D-00000008.8]</li>
</ul>
NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Service Start Time should not be part of that check.
