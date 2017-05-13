FindDocuments for LeafClass RG test

<h2>OneDocFindDocuments</h2>

<p>
A FindDocuments query is sent to the Responding Gateway (System under Test) for the
Single document Patient ID.  A single Document Entry is expected in the results.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>One DocumentEntry is returned
<li>Returned Home Community Id matches the Responding Gateway configuration in Toolkit
</ul>
</p>

<h2>OneDocGetDocuments</h2>

<p>
A GetDocuments query is sent to the Responding Gateway for the DocumentEntry.entryUUID submitted
with the test data.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>One DocumentEntry is returned
<li>Returned Home Community Id matches Community configuration
</ul>
</p>

<h2>TwoDocFindDocuments</h2>

<p>
A FindDocuments query is sent to the Responding gateway (System under Test) for the
Two document Patient ID.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>One DocumentEntries are returned
<li>Returned Home Community Ids match the Responding Gateway configuration in Toolkit
</ul>
</p>





