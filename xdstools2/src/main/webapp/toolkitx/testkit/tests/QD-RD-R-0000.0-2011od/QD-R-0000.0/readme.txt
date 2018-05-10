Responding Gateway responds to a basic Query Document query

Testing Tool (XDS toolkit) initiates QD synchronous Find Documents request to the System with the required parameters.  System responds with the matching documents metadata.

The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) 
<li>$XDSDocumentEntryPatientID = [P-000000010 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryType = [Demand Documents]</li>
<li>returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

The SUT is testing as an On-Demand system, it would send one On-Demand document with:

<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000010.27 OR (XDSDocumentEntry.patientID = [P-000000010 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000010.27]) </li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000010.27]</li>
<li>A match on: XDSDocument.objectType = [value from D-000000010.27]  </li>
</ul>

Assertions test the following:
<ul>
<li>ObjectRefCount - Query finds and returns 1 Approved DocumentEntries for this Patient ID.
</ul>
