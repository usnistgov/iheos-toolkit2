The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

SOAP Header = MP: MA Default Request (SUT)

Message Parameters
<ul>
<li>$XDSDocumentEntryPatientID = [P-0000000002 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryType = On-demand</li>
<li>returnType = LeafClass </li>
<li>request = synchronous</li>
<li>returnComposedObjects = true</li>
<li>Authorization Decision Statement.InstanceAccessConsentPolicy = urn:oid:2.16.840.1.113883.3.184.50.1</li>
</ul>

<b>Evaluation</b>
The System successfully processes the Request and returns a Response to the Testing Tool that contains one On-Demand document entry with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-00000002.1 OR (XDSDocumentEntry.patientID = [P-00000002 PID] AND XDSDocumentEntry.authorPerson = [value from D-00000002])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-00000002.1]</li>
<li>A match on: XDSDocument.objectType = [value from D-00000002.1]</li>
</ul>
NOTE: The creationTime, hash, and size SHALL not be included in the Response for an On-Demand system. The legalAuthenticator SHOULD not be included in the Response.
