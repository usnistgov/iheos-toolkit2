Query Single Responding Gateway test

<p>Although the Initiating Gateway is configured to forward requests to two
Responding Gateways, all sections of this test return content from a
single Responding Gateway.</p>


<h2>SingleDocumentFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway for the
Single Document Patient ID. For this patient one Responding Gateway will return a single
DocumentEntry.
uid
</p>

<h2>SingleDocumentGetDocuments</h2>
<p>
The DocumentEntry.entryUUID attribute is extracted from the above test results
and a GetDocuments stored query is sent to the Initiating Gateway.  The correct
Home Community Id is included in the request.
</p>

<h2>TwoDocumentsFindDocuments</h2>
<p>
A FindDocuments stored query is sent to the Initiating Gateway for the
Two Documents Patient ID. For this patient one Responding Gateway will return a two
DocumentEntries.
</p>


