<h3>Validate Cross Gateway Retrieve Image Document Set (RAD-75) Request from IIG 
SUT to RIG A simulator.</h3>

<p/>When the test section is run, the testkit will evaluate the RAD-75 Request
sent by the IIG SUT during the retrieve section of the test, comparing it
with a 'gold standard' response in the testplan. Validations include:
<ol>
<li/>That the request parses successfully. 
<li/>Correct StudyRequest element and studyInstanceUID attribute and value.
<li/>Correct SeriesRequest element and seriestInstanceUID attribute and value.
<li/>Correct DocumentRequest element with child elements:<ol>
<li/>HomeCommunityId with appropriate text.
<li/>RepositoryUniqueId with appropriate text.
<li/>DocumentUniqueId with appropriate text.</ol>
<li/>Valid TranferSyntaxUIDList element with child elements:<ol>
<li/>Valid TransferSyntaxUID element with text '1.2.840.10008.1.2.4.50'.
<li/>Valid TransferSyntaxUID element with text '1.2.840.10008.1.2.4.70'.
.</ol>
</ol>