
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

