Initiating Gateway sends a basic Query Document query

Initiate a FindDocuments Cross-Community Query from
your Initiating Gateway to the system listed above
in Supporting Environment Configuration - XXX__community1.
(XXX will reflect your Test Session name). Specifically
the query should go to the endpoint listed for Cross-
Community Query.

Use the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (SUT)</li>
<li>$XDSDocumentEntryPatientID = [P-000000002 PID]</li>
<li>$XDSDocumentEntryStatus = Approved OR Deprecated</li>
<li>$XDSDocumentEntryType = [Stable OR On-Demand Documents]  </li>
<li>returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
<li>returnComposedObjects = true</li>
</ul>
<b>NOTES</b>: 
Only send $XDSDocumentEntryType parameter for 2011
Recommendation is to send both Stable OR On-Demand Documents as the $XDSDocumentEntryType default for all queries.  

To validate this test, look again at the Supporting Environment 
Configuration above and select the Simulator Log for XXX__community1.
This will take you to the event log for the Responding Gateway
simulator you sent the FindDocuments request to.  Selecting the
event on the left will display details on the right.  When you
find the correct event (should be the first on the list - most
recent), declare this event as evidence of your testing.  The 
link to this information will be displayed at the top of the tool.
This link can be copied and reused later.

The finddocs section below shows an example of this query.


