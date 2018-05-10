Access Consent Policy - Retrieve Document

This is the third step of test ACP-PD-QD-RD-0000.0. Once your system has received the query response for the policy document, it is expected to retrieve it from the test tool.

The System transmits to the Testing Tool a synchronous Retrieve Documents request for one policy document using the following required parameters:

SOAP Header = MP: MA Default Request (SUT)

Message Parameters

<ul>
<li>RepositoryUniqueId: [Repository ID for D-000000002.XSSA-1]</li>
<li>DocumentUniqueId: [Document ID for D-000000002.XSSA-1]</li>
<li>homeCommunityId: [HCID for the Testing Tool]</li>
</ul>
