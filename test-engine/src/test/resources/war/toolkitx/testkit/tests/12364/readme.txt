MPQ - FindDocumentsForMultiplePatients Strored Query

This test relies on test 12361 to load the necessary test data.

Each sub-directory holds a testplan which tests a different
aspect of FindDocumentsForMultiplePatients Stored Query.

With multiple patient ids the following patterns

nothing - no codes or patient id is specified - must fail
classcode - classcode is specified but no Patient ID
eventcode - eventcode is specified but no Patient ID
hcftc - healthcareFacilityTypeCode is specified but no Patient ID
classcode_eventcode - select on both 
one_pid - include single Patient ID
multi_pid - include multiple Patient IDs


