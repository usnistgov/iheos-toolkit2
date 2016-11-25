PnR Single
<h2>Validate Key Object Selection Document, Single Image Study</h2>

<p/>Tests the ability of the Image Document Source actor (SUT) to send a Provide and
Register Imaging Document Set (RAD-68) transaction to a Repository/Registry
actor (Simulator), for a single DICOM image file.

<p/>The Imaging Document Source is required to submit a DICOM KOS object that
references an imaging study with a single image.

<ul>
<li/>Configure your Imaging Document Source System under Test (IDS SUT) to send
to the Repository/Registry simulator in the test environment.

<li>Configure your Imaging Document Source System under Test (IDS SUT)
Repository Unique ID to 1.3.6.1.4.1.21367.13.80.110. That means that the
value of (0040&nbsp;E011&nbsp;)Retrieve&nbsp;Location&nbsp;UID
(inside Referenced Series Sequence) will contain the value
1.3.6.1.4.1.21367.13.80.110. This is part of the test validation.</li>

<li/>Import the test image for the test data set IDS-DEPT001-a into your IDS SUT,
using whatever method is appropriate for your system.

<li/>Generate one KOS object per the XDS-I profile that references the single 
image in this study.

<li/>Submit that KOS object using a RAD-68 transaction to the
Repository/Registry simulator configured for your IDS SUT (e.g.: acme__rr).
Use the following patient identifier with the RAD-68 submission:
<blockquote>IDS-AD001-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</blockquote>
</ul>

<h3>Notes</h3>
<ul>
 <li>This test does not retrieve images using the RAD-16, RAD-55 or
  RAD-69 transactions. Other tests will use the same configuration/setup and will
  retrieve images using those transactions.</li>
 <li>The value for patient ID inside the original DICOM image is IDS-DEPT001-a.
  The value for patient ID in the XDS metadata is different (see above).
  This is consistent with the XDS model where one identifier is used within
  the department/enterprise but a different identifier is used in the context
  of the document sharing Affinity Domain.</li>
</ul>
