The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool)</li>
<li>$XDSDocumentEntryPatientID = [P-000000200 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryCreationTimeFrom = 20090514</li>
<li>$XDSDocumentEntryCreationTimeTo = 20090516</li>
<li>returnType = leafClass</li>
<li>SOAP request = synchronous</li>
</ul>

NOTE: various date formats are acceptable (see date/time format), and we are accepting partial use of date constraints as well: using only start time from/to, or using onlystop time from/to.

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000200.10 OR (XDSDocumentEntry.patientID = [P-000000200 PID] AND XDSDocumentEntry.authorPerson = [value from D+000000200.10])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000200.10]</li>
<li>A match on: XDSDocumentEntry.authorPerson = [value from D-000000200.10]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Creation Times should not be part of that check.