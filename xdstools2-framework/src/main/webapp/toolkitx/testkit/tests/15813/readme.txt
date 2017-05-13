Responding Gateway Retrieve test

<p>
The test depends on the successful results of test 15811.
</p>

<h2>RetrieveOne</h2>
<p>
Retrieve request is sent to the Responding Gateway for the single Document
corresponding to the first DocumentEntry returned from test 15811/TwoDocFindDocuments.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema
<li>One Document returned
<li>Returned Home Community Ids match the Responding Gateway configuration in Toolkit
<li>Returned Repository Unique ID matches the configuration in Toolkit
<li>Returned Document Unique ID matches the Retrieve request
<li>Returned Mime Type matches the test data
</ul>
</p>


<h2>RetrieveTwo</h2>
<p>
Retrieve request is sent to the Responding Gateway for the two Documents
corresponding to the DocumentEntries returned from test 15811/TwoDocFindDocuments.
</p>

<p>
The following validations are performed:
<ul>
<li>Schema
<li>Two Documents returned
<li>Returned Home Community Ids match the Responding Gateway configuration in Toolkit
<li>Returned Repository Unique ID matches the configuration in Toolkit
<li>Returned Document Unique IDs matches the Retrieve request
<li>Returned Mime Types matches the test data
</ul>
</p>



