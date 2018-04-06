MPQ - FindDocumentsForMultiplePatients Strored Query

This test relies on test 12361 to load the necessary test data. Two Patient IDs are provided: from Test 15819 and 15820. 

Several sections of this test issue the FindDocumentsForMultiplePatients query with no PatientID so you will have to clear your Registry contents to run this test multiple times.

Each section tests a different
aspect of FindDocumentsForMultiplePatients Stored Query.

Test sections:

nothing - no codes or patient id is specified - must fail <br />
classcode - classcode is specified but no Patient ID<br />
eventcode - eventcode is specified but no Patient ID<br />
hcftc - healthcareFacilityTypeCode is specified but no Patient ID<br />
classcode_eventcode - select on both <br />
one_pid - include single Patient ID in the request<br />
multi_pid - include multiple Patient IDs in the request<br />


