WADO Exception cases

<h2>WADO Retrieve, Exception cases</h2>

<h3>Purpose / Context</h3>


<h3>Prior to running this test:</h3>
This is a test of a number of exception cases related to the WADO Retrieve 
(Rad-55) transaction.<ol>
<li/>Study Instance UID and Series Instance UID reference known objects; SOP 
Instance UID does not reference a known object.
<li/>Study Instance UID and Series Instance UID reference known objects; SOP 
Instance UID references an object in a different series in the same study.
<li/>Study Instance UID and Series Instance UID reference known objects; SOP 
Instance UID references an object in a different study.
<li/>Series Instance UID and SOP Instance UIDs reference known objects; Study 
Instance UID is included but empty.
<li/>Study Instance UID and SOP Instance UIDs reference known objects; Series 
Instance UID is included but empty.
<li/>SOP Instance UID references a known object; Study Instance UID and Series 
Instance UID are included but empty.
</ol>

<h3>Prior to running this test:</h3>
Complete the setup steps listed in <i>Imaging Document Source PnR KOS: Single 
Image Study</i>. There is no reason to repeat the steps if the Imaging Document 
Source is ready for this retrieve test. This test uses the following patient 
identifier:<br/>
IDS-AD001-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO