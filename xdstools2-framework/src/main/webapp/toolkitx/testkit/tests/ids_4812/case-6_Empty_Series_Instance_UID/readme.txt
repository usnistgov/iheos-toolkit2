<h3>Send RAD-69 Request to IDS SUT - Empty Series Instance UID</h3>

<p/>This section tests the ability of the IDS SUT to respond correctly to a
Retrieve Imaging Document Set (RAD-69) Request where Study Instance UID and 
SOP Instance UID are known and the Series Instance UID is included, but is
blank.

<p/>When the test section is run:
<ol>
<li/>The Image Document Consumer simulator (IDC sim) sends a Retrieve Image 
Document Set Request (RAD-69 Request) to the Imaging Document Source System
Under Test (IDS SUT). This Request will reference a Study Instance UID and a
SOP Instance UID for a DICOM image file that exists in the test data, the 
Series Instance UID will be blank.
<li/>The IDS SUT is expected to process the Request and generate a response
indicating that the Request failed, and containing the appropriate error.
<li/>The testkit will validate the Response by comparing it with a 'gold
standard' Response contained in the testplan. Validations include:<ol>
<li/>That the RegistryResponse element status attribute contains the correct
Failure code.
<li/>That a RegistryError containing the correct values is returned. &#42;
</ol></ol>

&#42; A RegistryError is considered to be correct if it's errorCode and severity
attribute values match those in the 'gold standard' message.