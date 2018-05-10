Respond to a basic RD

Testing Tool (XDS Toolkit) initiates a synchronous Retrieve Documents request for two documents to the System. System responds with the requested documents.


The Testing Tool transmits to the System a synchronous Retrieve Documents request for one document using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool)</li>
<li>RepositoryUniqueId: [Repository ID for D-000000010.3]</li>
<li>DocumentUniqueId: [Document ID for D-000000010.3]</li>
<li>HomeCommunityId: [HCID for the System]</li>
<ul>

The following DeferredCreation document would be in the Response:

<ul>
<li>RepositoryUniqueId: [Repository ID for D-000000010.3]</li>
<li>DocumentUniqueId: [Document ID for D-000000010.3]</li>
<li>homeCommunityId: [HCID for the System]</li>
<li>Attribute NewRepositoryUniqueId should be present</li>
<li>Attribute NewDocumentUniqueId should be present</li>
</ul>