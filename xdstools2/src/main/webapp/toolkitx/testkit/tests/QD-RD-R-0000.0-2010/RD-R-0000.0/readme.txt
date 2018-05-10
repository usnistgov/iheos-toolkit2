Testing Tool (XDS Toolkit) initiates a synchronous Retrieve Documents request for two documents to the System. System responds with the requested documents.

If your system is an ATNA Secure Node/Secure Application, configure it to send audit messages to the Syslog Collector: https://validation.sequoiaproject.org/gss/syslog/list.seam

The Testing Tool transmits to the System a synchronous Retrieve Documents request for one document using the following required parameters:

If the SUT is testing as a Stable system, the following Stable document would need to be retrieved:
<ul>
<li>SOAP Header = MP: MA Default Request (TestTool)</li>
<li>RepositoryUniqueId: [Repository ID for D-000000010.1]</li>
<li>DocumentUniqueId: [Document ID for D-000000010.1]</li>
<li>HomeCommunityId: [HCID for the System]</li>
<ul>

The following Stable document would be in the Response:
<ul>
<li>RegistryResponse/@status:Success</li>
<li>DocumentResponse: 2 present, contains document D-000000010.1 and D-000000010.27</li>
<li>RepositoryUniqueId: [Repository ID for D-000000010.1]</li>
<li>DocumentUniqueId: [Document ID for D-000000010.1]</li>
<li>homeCommunityId: [HCID for the System]</li>
</ul>