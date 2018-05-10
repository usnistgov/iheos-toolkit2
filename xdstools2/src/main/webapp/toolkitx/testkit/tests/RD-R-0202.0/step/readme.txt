<b>QD</b>

The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000045 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryAuthorPerson =  Adam Hunter</li>
<li>returnType = LeafClass SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

The System successfully processes the Request and returns a QD Response to the Testing Tool that contains documents with the following objects:

One document with:
A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000045.1 OR ($XDSDocumentEntry.PatientID = [P-000000045 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000045.1])

<b>RD</b>
The Testing Tool sends a synchronous Retrieve Documents request to the System using an invalid DocumentUniqueId described as follows:

SOAP Header = MP: MA Default Request (TestTool) Message Parameters RepositoryUniqueId: [Repository ID for D-000000045.1] DocumentUniqueId: [Document Unique ID that the System never had] homeCommunityId: [HCID for the System]

<b>Expected Results</b>: The System does not process Request and returns an XDSDocumentUniqueId error to the Testing Tool containing:

RegistryResponse/RegistryErrorList: present, contains 1 RegistryError with @errorCode = XDSDocumentUniqueIdError
