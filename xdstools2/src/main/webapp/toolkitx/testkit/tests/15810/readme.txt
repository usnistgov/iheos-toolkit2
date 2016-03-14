Two Responding Gateway Response test

<p>The Initiating Gateway is configured to forward requests to both
Responding Gateways. This test expects content to be returned
from both Responding Gateways.</p>

<h2>TwoRGFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway with
the Patient ID that matches a single DocumentEntry held behind each 
Responding Gateway.
The following attributes are verified in the two DocumentEntries: correct Home Community 
ID, correct Mime Type, correct Repository Unique ID, and correct DocumentEntry Unique ID.
</p>



