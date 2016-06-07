Two Responding Gateway Response test

<p>The Initiating Gateway is configured to forward requests to both
Responding Gateways. This test expects content to be returned
from both Responding Gateways.</p>

<h2>TwoRGFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway with
the Patient ID that matches a single DocumentEntry held behind each 
Responding Gateway. In the Test Environment documentation this Patient ID is
referred to as
Both Communities have documents.
A DocumentEntry from each Community must be returned.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>Two DocumentEntries are returned
<li>Returned Home Community Ids matches Community configurations
<li>Returned Repository Unique Ids matches Community configurations
<li>Returned Document Mime Types match the test configuration
<li>Returned DocumentEntry Unique Ids match test configuration
</ul>
</p>

<h2>RG1Retrieve</h2>
<p>
This section can only be run if the above section TwoRGFindDocuments
is successful.
</p>

<p>
For the first DocumentEntry returned from TwoRGFindDocuments retrieve
the corresponding Document from that Community.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>One Document is returned
<li>Returned Home Community Id matches Community configuration
<li>Returned Repository Unique Id matches Community configuration
<li>Returned Document Mime Type match the test configuration
<li>Returned DocumentEntry Unique Id match test configuration
</ul>
</p>

<h2>RG2Retrieve</h2>
<p>
This section can only be run if the above section TwoRGFindDocuments
is successful.
</p>

<p>
For the second DocumentEntry returned from TwoRGFindDocuments retrieve
the corresponding Document from that Community.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>One Document is returned
<li>Returned Home Community Id matches Community configuration
<li>Returned Repository Unique Id matches Community configuration
<li>Returned Document Mime Type match the test configuration
<li>Returned DocumentEntry Unique Id match test configuration
</ul>
</p>

