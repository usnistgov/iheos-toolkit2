RAD-69 Exception Cases

<h2>Retrieve Image Document Set, Exception Cases</h2>

<p/>Purpose / Context

<p/>This is a test of a number of exception cases related to the Retrieve Imaging 
Document Set (Rad-69) transaction.
<h2>Case 0: Comparison transaction</h2>
This case uses the same transaction as the other cases, but has no errors. If
you are set up correctly, it should run and return a single image.
<h2>Case 1: Invalid RepositoryUID</h2>
Repository Unique ID in the request does not match the Repository Unique ID of 
the Imaging Document Source.
<h2>Case 2: Unknown SOP Instance UID</h2>
Study Instance UID and Series Instance UID reference known objects; SOP Instance 
UID does not reference a known object.
<h2>Case 3: SOP Instance UID not in requested series</h2>
Study Instance UID and Series Instance UID reference known objects; SOP Instance 
UID references an object in a different series in the same study.
<h2>Case 4: SOP Instance UID not in requested study</h2>
Study Instance UID and Series Instance UID reference known objects; SOP Instance 
UID references an object in a different study.
<h2>Case 5: Empty Study Instance UID</h2>
Series Instance UID and SOP Instance UIDs reference known objects; Study Instance 
UID is included but empty.
<h2>Case 6: Empty Series Instance UID</h2>
Study Instance UID and SOP Instance UIDs reference known objects; Series Instance 
UID is included but empty.
<h2>Case 7: Empty Study and Series Instance UIDs</h2>
SOP Instance UID references a known object; Study Instance UID and Series 
Instance UID are included but empty.
<h2>Case 8: Empty Transfer Syntax UID</h2>
Study Instance UID, Series Instance UID and SOP Instance UID properly reference 
an image; one transfer syntax UID is included but empty.
<h2>Case 9: Invalid Transfer Syntax UID</h2>
Study Instance UID, Series Instance UID and SOP Instance UID properly reference 
an image; one transfer syntax UID is included but it does not reference a known 
transfer syntax.