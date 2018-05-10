The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters </li>
<li>$XDSDocumentEntryPatientID= [P-000000007 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryClassCode = [code]^^[scheme] class code = 34117-2</li>
<li>class code scheme = 2.16.840.1.113883.6.1 returnType = LeafClass</li>
<li>SOAP request = synchronous returnComposedObjects = true</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000007.5 OR (XDSDocumentEntry.patientID = [P-000000007 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000007.5])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000007.5]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000007.5]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response.