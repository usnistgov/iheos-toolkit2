<b>QD</b>
The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000018 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryAuthorPerson = Adam Hunter OR $XDSDocumentEntryAuthorPerson = Dean Hunter OR $XDSDocumentEntryAuthorPerson = David DeGroot</li>
<li>returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>

The System successfully processes the Request and returns a QD Response to the Testing Tool that contains documents with the following objects:

<ul>
<li>One document with:
A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000040.1 OR ($XDSDocumentEntry.PatientID = [P-000000040 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000040.1])</li>

<li>Another document with:
A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000040.4 OR ($XDSDocumentEntry.PatientID = [P-000000040 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000040.4])</li>

<li>Another document with:
A 'DocumentMatch' of either: $XDSDocumentEntry.UniqueId = D-000000040.25 OR ($XDSDocumentEntry.PatientID = [P-000000040 PID] AND
$XDSDocumentEntry.AuthorPerson = [value from D-000000040.25])</li>
</ul>


<b>RD</b> 
The Testing Tool transmits to the System a synchronous Retrieve Documents request for two documents using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>RepositoryUniqueId: [Repository ID for D-000000040.1] 
DocumentUniqueId: [Document ID for D-000000040.1] homeCommunityId: [HCID for the System]</li>
<li>RepositoryUniqueId: [Repository ID for D-000000040.4] 
DocumentUniqueId: [Document ID for D-000000040.4] homeCommunityId: [HCID for the System]</li>
</ul>

<b>Expected Result</b>: The System returns to the Testing Tool an RD Response containing the requested document:

RegistryResponse/@status:Success
DocumentResponse: 2 present, contains documents D-000000040.1, D-000000040.4
