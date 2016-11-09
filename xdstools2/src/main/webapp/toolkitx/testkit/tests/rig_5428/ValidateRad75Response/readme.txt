<h3>Validate Cross Gateway Retrieve Image Document Set (RAD-69) Response from 
RIG SUT to IIG simulator.</h3>

<p/>When the test section is run, the testkit will evaluate the RAD-75 Response
sent by the RIG SUT during the retrieve section of the test, comparing it
with a 'gold standard' response in the testplan. Validations include:
<ol>
<li/>That the response parses successfully. 
<li/>Correct RegistryResponse status attribute value.
<li/>That two document were returned, with the correct DocumentUniqueId and
mimeType values.
</ol>