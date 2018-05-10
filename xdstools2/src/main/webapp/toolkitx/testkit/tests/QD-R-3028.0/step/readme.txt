The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000024 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>
$XDSDocumentEntryHealthcareFacilityTypeCode = [code]^^[scheme] healthcare 
facility type code = 36125001
healthcare facility type code scheme = 2.16.840.1.113883.6.96 returnType = LeafClass</li>
<li>SOAP request = synchronous returnComposedObjects = true</li>


<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains the following objects:

One document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000024.1 OR (XDSDocumentEntry.patientID = [P-000000024 PID] AND
$XDSDocumentEntry.authorPerson = [value from D-000000024.1])</li>
<li>A match on: $XDSDocumentEntry.status = [value from D-000000024.1]</li>
<li>A match on: $XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000024.1]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000024.4 OR (XDSDocumentEntry.patientID = [P-000000024 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000024.4])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000024.4]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000024.4]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response.
