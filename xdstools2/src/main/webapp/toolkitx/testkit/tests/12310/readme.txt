FindDocuments for LeafClass

Test the FindDocuments for LeafClass Stored Query against the Responding Gateway.

This test depends on test 12318 to initialize test data.

Assertions test the following conditions:

<ul>
<li>ExtrinsicObjectCount - Query finds 2 Approved DocumentEntries and returns 2 DocumentEntries for this Patient ID.
</ul>

Implicit Assertions:

<ul>
<li>Metadata validates against Schema
<li>Metadata validates against XDS Metadata Validator
</ul>

