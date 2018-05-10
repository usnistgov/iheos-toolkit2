Responding Gateway responds to a basic Query Document query

Testing Tool (XDS toolkit) initiates QD synchronous Find Documents request to the System with the required parameters.  System responds with the matching documents metadata.

The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) 
<li>$XDSDocumentEntryPatientID = [P-000000010 PID]</li>
<li>$XDSDocumentEntryStatus = DeferredCreation</li>
<li>returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

The System successfully processes the Request and returns a Response to the Testing Tool that contains one DeferredCreation document with
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000010.3 OR (XDSDocumentEntry.patientID = [P-000000010 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000010.3]) </li>
</ul>

Assertions test the following:
<ul>
<li>ObjectRefCount - Query finds and returns 1 DeferredCreation document for this Patient ID.
</ul>
