


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

