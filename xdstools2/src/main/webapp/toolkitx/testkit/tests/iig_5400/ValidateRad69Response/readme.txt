<h3>Validate Retrieve Image Document Set (RAD-69) Response from IIG SUT to IDC
simulator.</h3>

<p/>When the test section is run, the testkit will evaluate the RAD-69 Response
sent by the IIG SUT during the retrieve section of the test, comparing it
with a 'gold standard' response in the testplan. Validations include:
<ol>
<li/>That the response parses successfully. 
<li/>Correct RegistryResponse status attribute value.
<li/>That a single document was returned, with the correct DocumentUniqueId and
mimeType values.
</ol>