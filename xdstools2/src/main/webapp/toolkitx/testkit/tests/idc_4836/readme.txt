RAD-55 WADO Retrieve: Multi Image Study

  <h2>Imaging Document Consumer RAD-55 WADO Retrieve: Multi Image Study</h2>
  <h3>Purpose / Context</h3> The Imaging Document Consumer performs:<ul>
    <li>RAD-55 WADO Retrieve to retrieve the specified objects.</li>
  </ul>
  <p>The test points are:<ul>
    <li>The Imaging Document Consumer can perform a valid RAD-55 WADO Retrieve independent of
      other transactions. <li/>The Imaging Document Consumer is able to render the retrieved
      images or provide other evidence that the images were retrieved and somehow made available
      for further use.</li>
  </ul>
  </p>
  <h3>Test Steps</h3>
  <i>Setup</i><ul>
    <li>Images and KOS objects are pre-loaded into the testing system actor simulators.</li>
    <li>The Imaging Document Consumer will query the actor simulators directly.</li>
  </ul>
  <i>Instructions</i>
  <ol>
    <li>The Imaging Document Consumer is instructed to query the Registry/Repository simulator for
      the KOS object for the patient with patient ID:<br/>
      IDCAD021-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</li>
    <li>The Imaging Document Consumer should both query for and retrieve the KOS object.</li>
    <li>The Imaging Document Consumer is instructed to use a RAD-55 (DICOM WADO) transaction to
      retrieve the imaging study with multiple images from the Imaging Document Source
      simulator.</li>
    <li>The Imaging Document Consumer is instructed to render the retrieved images and present the
      rendered data through an Internet desk sharing application. If the Imaging Document Consumer
      is not an application with a user facing GUI, the Imaging Document Consumer must somehow
      demonstrate that the images were indeed retrieved and made available for other use.</li>
  </ol>
  <i>Validation</i>
  <ol>
    <li>Test Manager: Verify that the retrieved images are the correct ones. One study with a 20
      image series of MR images and a series of 16 MR images <li/>Test Manager: They should contain
      the following attributes: <table>
        <tr><td>(0008,0050) SH #6 [IDC021-a]</td><td>AccessionNumber</td></tr>
        <tr><td>(0010,0010) PN #12 [Multi^Wado^a]</td><td>PatientName</td></tr>
        <tr><td>(0010,0020) LO #10 [IDCDEPT021-a]</td><td>PatientID</td></tr>
      </table></li>
    <li>Test Manager: Using an Internet desk sharing application, review the rendered study
      presented by the Imaging Document Consumer. If the Imaging Document Consumer is not an
      application with a user facing GUI, the Imaging Document Consumer must somehow demonstrate
      that the images were indeed retrieved and made available for other use.</li>
    <li>Test Manager: Verify by hand that the request is of the format listed in the example below.
      Verify that these attributes are present in the request with appropriate values. There are two
      series with different UIDs; see below.There are 36 images in the study; the objectUIDs number
      from 031.1 to 031.37<ol type="a">
        <li>requestType (shall be WADO)</li>
        <li>studyUID 1.3.6.1.4.1.21367.201599.1.201604021948031</li>
        <li>seriesUID 1.3.6.1.4.1.21367.201599.2.201604021948031<br/> Or
          1.3.6.1.4.1.21367.201599.2.201604021948031.21</li>
        <li>objectUID 1.3.6.1.4.1.21367.201599.3.201604021948031.1</li>
        <li>contentType (shall be application/dicom or image/jpeg)</li>
        <br/> GET
            /wado?requestType=WADO&studyUID=1.3.6.1.4.1.21367.201599.1.xxx&seriesUID=1.3.6.1.4.1.21367.201599.2.xxx&objectUID=1.3.6.1.4.1.21367.201599.3.xxx&contentType=application/dicom&overlays=false
            HTTP/1.1
      </ol>
    </li>
  </ol>