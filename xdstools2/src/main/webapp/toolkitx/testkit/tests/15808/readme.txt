Query Single Responding Gateway test

<p>Although the Initiating Gateway is configured to forward requests to two
Responding Gateways, all sections of this test expect content to be returned from a
single Responding Gateway described above as Community 1.</p>


<h2>SingleDocumentFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway for the
Single Document Patient ID (labeled Single document in Community 1 above).
For this patient one Responding Gateway, Community 1, will return a single
DocumentEntry.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>Returned Home Community Id matches Community configuration
<li>Returned Document Mime Type matches  configuration
<li>Returned DocumentEntry.uniqueId matches  configuration
</ul>
</p>

<h2>SingleDocumentGetDocuments</h2>

<p>
A GetDocuments stored query is used to retrieve the DocumentEntry loaded into
Community 1 under the Patient ID labeled Single document in Community 1 above.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>Returned Home Community Id matches Community configuration
<li>A single DocumentEntry is returned
</ul>
</p>

<h2>TwoDocumentsFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway for the
Two Documents Patient ID. For this patient one Responding Gateway will return two
DocumentEntries, both coming from Community 1.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema and metadata formatting
<li>Two DocumentEntries are returned
<li>Returned Home Community Id matches Community configuration (on each returned DocumentEntry)
</ul>
</p>

