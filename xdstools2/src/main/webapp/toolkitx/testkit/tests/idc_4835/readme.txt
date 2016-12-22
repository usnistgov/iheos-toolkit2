RAD-55 WADO Retrieve: Single Image Study
<h2>Imaging Document Consumer RAD-55 WADO Retrieve: Single Image Study</h2>

<h3>Purpose / Context</h3>
The Imaging Document Consumer performs:<ul>
<li/>RAD-55 WADO Retrieve to retrieve the specified object.
</ul>
The test points are:<ul>
<li/>he Imaging Document Consumer can perform a valid RAD-55 WADO Retrieve 
independent of other transactions.
</ul>
<h3>Test Steps</h3>
<i>Setup</i><ul>
<li/>Images and KOS objects are pre-loaded into the testing system actor simulators.
<li/>The Imaging Document Consumer will query the actor simulators through the proxy.
</ul>
<i>Instructions</i><ol>
<li/>The Imaging Document Consumer is instructed to query the Registry/Repository 
simulator for the KOS object for the patient with patient ID:<br/> 
IDCAD011-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
<li/>The Imaging Document Consumer should both query for and retrieve the KOS 
object.
<li/>The Imaging Document Consumer is instructed to use a RAD-55 (DICOM WADO) 
transaction to retrieve the imaging study with one image from the Imaging 
Document Source simulator.
</ol>
<i>Validation</i><ol>
<li/>Test Manager: Detailed instructions on validating the query and retrieve of 
the KOS are outlined in the test: 'Imaging Document Consumer Retrieve KOS: Single 
Image Study'. Follow verification steps 1 through 6.
<li/>Test Manager: Verify that the retrieved KOS object is the correct one.  
It should contain the following attributes:<table>
<tr><td>(0008,0018) UI [1.3.6.1.4.1.21367.201599.3.201603032140031.1]</td><td># SOPInstanceUID</td></tr>
<tr><td>(0008,0050) SH [IDC011-a]</td><td># AccessionNumber</td></tr>
<tr><td>(0010,0010) PN [Single^Wado^a]</td><td># PatientName</td></tr>
<tr><td>(0010,0020) LO [IDCDEPT011-a]</td><td># PatientID</td></tr></table>
<li/>Test Manager: Locate the RAD-55 request message in the proxy.<ol type="a">
<li/>Browse to http://gazelle-proxy-host:8380/proxy
<li/>Click "Messages list" in top menu bar
<li/>Select Message type HTTP in the Dynamic message type search.
<li/>Locate the  request and response in the message list.
<li/>Click on the "detail" link of the request portion of the transaction. It is 
labeled with the white arrow in the green circle.
<li/>The "Headers" section will contain the GET request parameters.</ol>
<li/>Test Manager: Take a snapshot of the evidence.
<li/>Test Manager: Verify by hand that the request is of the format listed in 
the example below. Verify that these attributes are present in the request with 
appropriate values:<ol type="a">
<li/>requestType (shall be WADO)
<li/>studyUID    1.3.6.1.4.1.21367.201599.1.201604021948013
<li/>seriesUID   1.3.6.1.4.1.21367.201599.2.201604021948013
<li/>objectUID   1.3.6.1.4.1.21367.201599.3.201604021948014
<li/>contentType (shall be application/dicom or image/jpeg)</ol><br/>
GET /wado?requestType=WADO&studyUID=1.3.6.1.4.1.21367.201599.1.201604021948013&seriesUID=1.3.6.1.4.1.21367.201599.2.201604021948013&objectUID=1.3.6.1.4.1.21367.201599.3.201604021948014&contentType=application/dicom&overlays=false HTTP/1.1
<li/>Test Manager: Verify that the HTTP Accept field contains one of the following values:<ol type="a">
<li/>application/dicom
<li/>image/jpeg
<li/>If other values are included, you will need to make a judgment call on whether 
they make sense for an image retrieve operation.</ol></ol>