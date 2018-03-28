Export study: Base test description
<h2>Edge Server Export Study to Clearing House</h2>
<h3>Base Test Description</h3>


Test A-7002-01 is a reference description and not a test that is actually executed.
The steps described in this test are referenced by other tests.
These tests assume a single patient with a single imaging exam (one Accession Number.
These tests also send data to a test clearinghouse (i.e. NIST XDS Simulator) rather
than to a production clearinghouse. This allows us to examine the meta-data that was
transmitted.
<ul>
<li/>Populate Edge Server with a single imaging exam.
<li/>Manually prompt prompt Edge Server to transmit the imaging exam to the Test Clearinghouse.
<li/>Check the following at the NIST repository:<ul>
<li/>Is the meta-data sent by the Transfer-App legal?
<li/>Does the meta-data sent by the Transfer-App match expected values
(coded entries, patient identifiers, hash values).
<li/>Do the DICOM images that were sent match the images on the edge
device (binary compare)?
<li/>Does the Key Object note in the Submission Set reference all DICOM
objects and no additional objects?
</ol></ul>

