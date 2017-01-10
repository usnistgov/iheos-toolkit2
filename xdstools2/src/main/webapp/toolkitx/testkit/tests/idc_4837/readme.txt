RAD-69 Retrieve: Single Image Study
  <h2>Imaging Document Consumer RAD-69 Retrieve Imaging Document Set: Single Image Study</h2>
  <h3>Purpose / Context</h3>
  The Imaging Document Consumer performs:
  <ul>
    <li>RAD-69 Retrieve Imaging Document Set to retrieve the study for the specified image.</li>
  </ul>
  The test points are:<ul>
    <li>The Imaging Document Consumer can perform a valid RAD-69 Retrieve Images
    independent of other transactions.</li>
  </ul>
  <h3>Test Steps</h3>
  <i>Setup</i>
  <ul>
    <li>Images and KOS objects are pre-loaded into the testing system actor
    simulators.</li>
    <li>The Imaging Document Consumer will query the actor simulators directly.</li>
  </ul>
  <i>Instructions</i>
  <ol>
    <li>The Imaging Document Consumer is instructed to query the Registry/Repository
    simulator for the KOS object for the patient with patient ID: <br/>
    IDCAD013-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</li>
    <li>The Imaging Document Consumer should both query for and retrieve the KOS
    object.</li>
    <li>The Imaging Document Consumer is instructed to use a RAD-69 Retrieve
    Imaging Document Set) transaction to retrieve the imaging study with one image
    from the Imaging Document Source simulator.</li>
  </ol>
  <i>Validation</i>
  <ol>
    <li>Test Manager: Detailed instructions on validating the query and retrieve of
      the KOS are outlined in the test: ids_4831 'Retrieve KOS: Single Image Study'.
      Follow verification steps 1 through 6.</li>
    <li>Test Manager: Verify that the retrieved KOS object is the correct one.  It
    should contain the following attributes:<table>
      <tr><td>(0008,0018) UI #44 [1.3.6.1.4.1.21367.201599.3.201603032140034.1]
      </td><td>SOPInstanceUID</td></tr>
      <tr><td>(0008,0050) SH #6 [IDC013-a]</td><td>AccessionNumber</td></tr>
      <tr><td>(0010,0010) PN #12 [Single^Soap-a]</td><td>PatientName</td></tr>
      <tr><td>(0010,0020) LO #10 [IDCDEPT013-a]</td><td>PatientID</td></tr>
    </table></li>
    <li>Test Manager: Locate the RAD-69 request message in the simulator logs.</li>
    <li>Test Manager: Take a snapshot of the evidence.</li>
    <li>Test Manager: Verify by hand that the request is of the format listed
    below. Verify that these elements are present in the request:
      <ol type="a">
        <li>StudyRequest
          <ol type="i">
            <li>SeriesRequest
              <ol>
                <li>DocumentRequest</li>
              </ol>
            </li>
          </ol>
        </li>
        <li>TransferSyntaxUIDList
          <ol type="i"><li>TransferSyntaxUID</li>
          </ol>
        </li>
      </ol>
    </li>
  </ol>
  <textarea style="border:none;" rows="17" cols="100" disabled>
    <iherad:RetrieveImagingDocumentSetRequest
      &nbsp;xmlns:iherad="urn:ihe:rad:xdsi-b:2009"
      &nbsp;xmlns:ihe="urn:ihe:iti:xds-b:2007"
      &nbsp;xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      &nbsp;<iherad:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201602281031046">
        &nbsp;&nbsp;<iherad:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201602281031046">
          &nbsp;&nbsp;&nbsp;<ihe:DocumentRequest>
            &nbsp;&nbsp;&nbsp;&nbsp;<ihe:RepositoryUniqueId>1.1.4567332.10.99</ihe:RepositoryUniqueId>
            &nbsp;&nbsp;&nbsp;&nbsp;<ihe:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201602281031046.1</ihe:DocumentUniqueId>
            &nbsp;&nbsp;&nbsp;</ihe:DocumentRequest>
          &nbsp;&nbsp;</iherad:SeriesRequest>
        &nbsp;</iherad:StudyRequest>
      &nbsp;<iherad:TransferSyntaxUIDList>
        &nbsp;&nbsp;<iherad:TransferSyntaxUID>1.2.840.10008.1.2.1</iherad:TransferSyntaxUID>
        &nbsp;</iherad:TransferSyntaxUIDList>
    </iherad:RetrieveImagingDocumentSetRequest>
  </textarea>