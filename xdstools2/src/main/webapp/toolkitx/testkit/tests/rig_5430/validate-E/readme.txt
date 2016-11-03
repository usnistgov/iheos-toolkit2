<h3>Validate Retrieve Image Document Set (RAD-69) Request from RIG SUT to IDS E 
simulator.</h3>

<p/>When the test section is run, the testkit will evaluate the RAD-69 Request
sent by the RIG SUT during the retrieve section of the test, comparing it
with a 'gold standard' response in the testplan. Validations include:
<ol>
<li/>That the request parses successfully. 
<li/>Correct StudyRequest elements and studyInstanceUID attributes and values.
<li/>Correct SeriesRequest elements and seriestInstanceUID attributes and values.
<li/>Correct DocumentRequest elements with child elements:<ol>
<li/>HomeCommunityId with appropriate text.
<li/>RepositoryUniqueId with appropriate text.
<li/>DocumentUniqueId with appropriate text.</ol>
<li/>Valid TranferSyntaxUIDList element with child element:<ol>
<li/>Valid TransferSyntaxUID element with appropriate text.</ol>
</ol>