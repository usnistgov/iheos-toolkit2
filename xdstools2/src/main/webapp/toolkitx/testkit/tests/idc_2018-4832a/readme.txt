RAD-55 WADO Retrieve: Single Image Study
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Consumer: RAD-55 WADO Retrieve: Single Image
      Study</title>
  </head>
  <body>
    <h2>Imaging Document Consumer RAD-55 WADO Retrieve: Single Image
      Study</h2>
    <h3>Purpose / Context</h3>
    The Imaging Document Consumer performs:
    <ul>
      <li>RAD-55 WADO Retrieve to retrieve the specified object.</li>
    </ul>
    The test points are:
    <ul>
      <li>The Imaging Document Consumer can perform a valid RAD-55 WADO
        Retrieve independent of other transactions.</li>
    </ul>
    <h3>Test Steps</h3>
    <i>Setup</i>
    <ul>
      <li>Images and KOS objects are pre-loaded into the testing system
        actor simulators.</li>
      <li>The Imaging Document Consumer will query the actor simulators.</li>
    </ul>
    <i>Instructions</i>
    <ol>
      <li>The Imaging Document Consumer is instructed to query the
        Registry/Repository simulator for the KOS object for the patient
        with patient ID:<br>
        IDCAD011-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO</li>
      <li>The Imaging Document Consumer should both query for and
        retrieve the KOS object.</li>
      <li>The Imaging Document Consumer is instructed to use a RAD-55
        (DICOM WADO) transaction to retrieve the imaging study with one
        image from the Imaging Document Source simulator.</li>
    </ol>
    <i>Validation</i>
    <ol>
      <li>Test Manager: Detailed instructions on validating the query
        and retrieve of the KOS are outlined in the test: ids_4831
        'Retrieve KOS: Single Image Study'. Follow verification steps 1
        through 6.</li>
      <li>Test Manager: Verify that the retrieved KOS object is the
        correct one. It should contain the following attributes:
        <table>
          <tbody>
            <tr>
              <td>(0008,0018) UI
                [1.3.6.1.4.1.21367.201599.3.201603032140031.1]</td>
              <td># SOPInstanceUID</td>
            </tr>
            <tr>
              <td>(0008,0050) SH [IDC011-a]</td>
              <td># AccessionNumber</td>
            </tr>
            <tr>
              <td>(0010,0010) PN [Single^Wado^a]</td>
              <td># PatientName</td>
            </tr>
            <tr>
              <td>(0010,0020) LO [IDCDEPT011-a]</td>
              <td># PatientID</td>
            </tr>
          </tbody>
        </table>
      </li>
      <li>Test Manager: Locate the RAD-55 request message in the
        simulator test logs.</li>
      <li>Test Manager: Take a snapshot of the evidence.</li>
      <li>Test Manager: Verify by hand that the request is of the format
        listed in the example below. Verify that these attributes are
        present in the request with appropriate values:
        <ol type="a">
          <li>requestType (shall be WADO)</li>
          <li>studyUID 1.3.6.1.4.1.21367.201599.1.201604021948013</li>
          <li>seriesUID 1.3.6.1.4.1.21367.201599.2.201604021948013</li>
          <li>objectUID 1.3.6.1.4.1.21367.201599.3.201604021948014</li>
          <li>contentType (shall be application/dicom or image/jpeg)</li>
        </ol>
      </li>
      <br>
      GET
      /wado?requestType=WADO&amp;studyUID=1.3.6.1.4.1.21367.201599.1.201604021948013&amp;seriesUID=1.3.6.1.4.1.21367.201599.2.201604021948013&amp;objectUID=1.3.6.1.4.1.21367.201599.3.201604021948014&amp;contentType=application/dicom&amp;overlays=false



      HTTP/1.1
      <li>Test Manager: Verify that the HTTP Accept field contains one
        of the following values:
        <ol type="a">
          <li>application/dicom</li>
          <li>image/jpeg</li>
          <li>If other values are included, you will need to make a
            judgment call on whether they make sense for an image
            retrieve operation.</li>
        </ol>
      </li>
    </ol>
  </body>
</html>
