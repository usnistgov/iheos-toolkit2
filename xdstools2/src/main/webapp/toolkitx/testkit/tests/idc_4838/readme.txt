RAD-69 Retrieve: Multi Image Study
  <h2>Imaging Document Consumer RAD-69 Retrieve Imaging Document Set: Multi Image Study</h2>
  <h3>Purpose / Context</h3> The Imaging Document Consumer performs:<ul>
    <li>RAD-69 Retrieve Imaging Document Set to retrieve the specified objects.</li>
  </ul> The test points are:<ul>
    <li>The Imaging Document Consumer can perform a valid RAD-69 Retrieve Imaging Document Set
      independent of other transactions.</li>
    <li>The Imaging Document Consumer is able to render the retrieved images or provide other
      evidence that the images were retrieved and somehow made available for further use.</li>
  </ul>
  <h3>Test Steps</h3>
  <i>Setup</i>
  <ul>
    <li>Images and KOS objects are pre-loaded into the testing system actor simulators.</li>
    <li>The Imaging Document Consumer will query the actor simulators directly.</li>
  </ul>
  <i>Instructions</i>
  <ol>
    <li>The Imaging Document Consumer is instructed to query the Registry/Repository simulator for
      the KOS object for the patient with patient ID: <br/>
      IDCAD023-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</li>
    <li>The Imaging Document Consumer should both query for and retrieve the KOS object.</li>
    <li>The Imaging Document Consumer is instructed to use a RAD-69 (Retrieve Imaging Document Set )
      transaction to retrieve the imaging study with multiple images from the Imaging Document
      Source simulator.</li>
    <li>The Imaging Document Consumer is instructed to render the retrieved images and present the
      rendered data through an Internet desk sharing application. If the Imaging Document Consumer
      is not an application with a user facing GUI, the Imaging Document Consumer must somehow
      demonstrate that the images were indeed retrieved and made available for other use.</li>
  </ol>
  <i>Validation</i>
  <ol>
    <li>Test Manager: Verify that the retrieved images are the correct ones. One study with a 20
      image series of MR images and a series of 16 MR images.</li>
    <li>Test Manager: They should contain the following attributes: <table>
        <tr><td>(0008,0050) SH #6 [IDC023-a]</td><td>AccessionNumber</td></tr>
        <tr><td>(0010,0010) PN #12 [Multi^Soap^a]</td><td>PatientName</td></tr>
        <tr><td>(0010,0020) LO #10 [IDCDEPT023-a]</td><td>PatientID</td></tr>
      </table>
    </li>
    <li>Test Manager: Using an Internet desk sharing application, review the rendered study
      presented by the Imaging Document Consumer. If the Imaging Document Consumer is not an
      application with a user facing GUI, the Imaging Document Consumer must somehow demonstrate
      that the images were indeed retrieved and made available for other use.</li>
  </ol>
