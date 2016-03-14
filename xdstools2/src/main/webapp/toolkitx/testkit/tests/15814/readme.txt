Initiating Gateway negative tests

<h2>XDSRegistryError</h2>
<p>
The FindDocuments query sent through the Initiating Gateway SUT will
return an XDSRegistryError error from Community 1 and a single DocumentEntry
from Community 2. The Initiating Gateway must pass both the error and the
DocumentEntry back to the Document Consumer and label the overall response as
a PartialSuccess.
</p>