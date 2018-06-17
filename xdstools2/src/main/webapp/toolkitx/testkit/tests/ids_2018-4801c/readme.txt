RAD-68: PnR KOS for Multi Modality Study
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Source: RAD-68: PnR KOS for Multi Modality
      Study</title>
  </head>
  <body>
    <h2>RAD-68: PnR KOS for Multi Modality Study</h2>
    <p>Tests the ability of the Image Document Source actor (SUT) to
      send a Provide and Register Imaging Document Set (RAD-68)
      transaction to a Repository/Registry actor (Simulator). The
      Imaging Document Source is required to submit a DICOM KOS object
      that references an imaging study with images from different
      modalities. This means that the DocumentEntry.eventCode metadata
      values will contain separate entries for the different modality
      values.<br>
      <br>
    </p>
    <h3>Mappings</h3>
    <table border="1">
      <tbody>
        <tr>
          <th>DICOM Patient Identifier</th>
          <th>XDS DocumentEntry.patientId</th>
        </tr>
        <tr>
          <td>TCGA-G4-6304<br>
          </td>
          <td>IDS_2018-4801c</td>
        </tr>
      </tbody>
    </table>
    <h3>Instructions</h3>
    <ul>
    </ul>
    <ol>
    </ol>
    This test follows the same pattern as ids_2018-4801a. Follow the
    instructions from ids_2018-4801a with these differences:<br>
    <ul>
      <li>Data set is TCGA-G4-6304</li>
      <li>Patient identifier mapping is shown in the table above.<br>
      </li>
    </ul>
    <h3>Notes</h3>
    <p>The KOS object for this test will reference multiple images in
      multiple series.<br>
      Some of the metadata elements that are based on the original image
      content (e.g., Accession Number, Modality) will differ from the
      values in ids_2018-4801a.<br>
    </p>
    <h3>Data Requirements</h3>
    <h4>KOS Requirements</h4>
    <p>See instructions in ids_2018_4800.<br>
    </p>
    <h4>Metadata Requirements</h4>
    See instructions in ids_2018_4800.
  </body>
</html>
