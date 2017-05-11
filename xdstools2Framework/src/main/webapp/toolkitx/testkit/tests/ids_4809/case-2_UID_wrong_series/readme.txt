<h3>Send RAD-55 Request to IDS SUT - SOP Instance from wrong series.</h3>

<p/>This section tests the ability of the IDS SUT to correctly respond to a
RAD-55 WADO Request which contains valid Study and Series Instance UIDs, and a
SOP Instance UID which represents an image which is in the indicated study, but 
not in the indicated series.

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) sends a WADO Retrieve Image 
Document Set Request (RAD-55 Request) to the Imaging Document Source System
Under Test (IDS SUT), containing valid Study and Series Instance UIDs, and a
SOP Instance UID from a different series.
<li/>The IDS SUT is expected to process the test and generate a response
containing the appropriate response status code.
<li/>The IDC sim will process the RAD-55 response.
<li/>The test will pass if the response contains the correct status code.
</ol>