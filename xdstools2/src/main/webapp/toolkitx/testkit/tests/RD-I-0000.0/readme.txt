Initiating Gateway sends a basic Retrieve Document request

Initiate a Cross-Community Retrieve from your 
Initiating Gateway to the system listed above
in Supporting Environment Configuration - XXX__community1.
(XXX will reflect your Test Session name). Specifically
the query should go to the endpoint listed for Cross-
Community Retrieve.

Your System transmits to the Testing Tool a synchronous Retrieve Documents request for one document using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (SUT) Message Parameters</li>
<li>RepositoryUniqueId: [Repository ID for D-000000002.1]</li>
<li>DocumentUniqueId: [Document ID for D-000000002.1]</li>
<li>homeCommunityId: [HCID for the Testing Tool]</li>
</ul>

To validate this test, look again at the Supporting Environment 
Configuration above and select the Simulator Log for XXX__community1.
This will take you to the event log for the Responding Gateway
simulator you sent the FindDocuments request to.  Selecting the
event on the left will display details on the right.  When you
find the correct event (should be the first on the list - most
recent), declare this event as evidence of your testing.  The 
link to this information will be displayed at the top of the tool.
This link can be copied and reused later.

