<p/>When the test section is run, the testkit will evaluate the Key Object Set
DICOM object sent to the repository/registry by the IDS SUT. Values in the
KOS will be compared with those in a 'gold standard' KOS object which is part of
the testkit. Validations include:
<ol>
<li/>The KOS is a valid DICOM object file.
<li/>The SOP Class UID (0008,0016) values are the same.
<li/>The SOP Instance UID (0008,0018) value is NOT the same as that in the 
original image.
<li/>The Patient Name (0010,0010) values are the same.
<li/>The Patient ID (0010,0020) values are the same. This test gives only a 
warning if the two values do not match.
<li/>The Patient Birth Date (0010,0030) values are the same.
<li/>The Patient Sex (0010,0040) values are the same.
<li/>The Accession Number (0008,0050) values are the same.
<li/>The Modality (0008,0060) value is "KO".
<li/>The Study Instance UID (0020,000D) values are the same.
<li/>The Series Instance UID (0020,000E) value  is NOT the same as that in the 
original image.
<li/>The Current Requested Procedure Evidence Seq (0040,A730) is examined:<ol>
<li/>For matching Study Instance UID (0020,000D) values.
<li/>For appropriate subordinate Referenced Series Seq (0008,1115) entries with:<ol>
<li/>The Series Instance UID (0020,000E) values are the same.
<li/>The Retrieve AE Title (0008,0054) is present and not empty.
<li/>The Retrieve Location UID (0040,E011) values are the same.
<li/>For appropriate Referenced SOP Seq (0008,1199) entries with:<ol>
<li/>The Referenced SOP Class UID (0008,1150) values are the same.
<li/>The Referenced SOP Instance UID (0008,1155) values are the same.</ol></ol></ol>