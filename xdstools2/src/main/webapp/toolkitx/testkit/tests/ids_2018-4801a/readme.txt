RAD-68: PnR KOS for Single Image Study
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Source: RAD-68: PnR KOS for Single Image
      DICOM Study</title>
  </head>
  <body>
    <h2>RAD-68: PnR KOS for Single Image Study<br>
    </h2>
    <p>Tests the ability of the Image Document Source actor (SUT) to
      send a Provide and Register Imaging Document Set (RAD-68)
      transaction to a Repository/Registry actor (Simulator). The
      Imaging Document Source is required to submit a DICOM KOS object
      that references an imaging study with a single image. As part of
      this test, you will need to map the Patient Identifier value found
      in the DICOM image to the DocumentEntry.patientId value in the XDS
      metadata.<br>
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
          <td>C3L-00277</td>
          <td>IDS_2018-4801a</td>
        </tr>
      </tbody>
    </table>
    <h3>Instructions</h3>
    <ul>
    </ul>
    <ol>
      <li>Read requirements in test ids-2018_4800.</li>
      <ul>
        <li>Configuration</li>
        <li>KOS Content</li>
        <li>XDS Metadata<br>
        </li>
      </ul>
      <li>Read the KOS document and metadata requirements at the bottom
        of this narrative.<br>
      </li>
      <li>Import the test image for the test data set C3L-00277 into
        your IDS SUT, using whatever method is appropriate for your
        system. </li>
      <li>Generate one KOS object per the XDS-I profile that references
        the single image in this study.</li>
      <li>Submit that KOS object using a RAD-68 transaction to the
        Repository/Registry simulator configured for your IDS SUT (e.g.:
        acme__rr). Use the following patient identifier with the RAD-68
        submission:
        <blockquote>IDS_2018-4801^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO</blockquote>
      </li>
      <li>Activate the test button to search for the KOS submission and
        then validate the KOS object and XDS metadata.<br>
      </li>
    </ol>
    <h3>Notes</h3>
    <ul>
      <li>This test does not retrieve images using the RAD-55 or RAD-69
        transactions. Other tests will use the same configuration/setup
        and will retrieve images using those transactions.</li>
      <li>The value for Patient Identifier inside the original DICOM
        image is C3L-00277. The value for DocumentEntry.patientId in the
        XDS metadata is different (see above). This is consistent with
        the XDS model where one identifier is used within the
        department/enterprise but a different identifier is used in the
        context of the document sharing Affinity Domain.</li>
      <li>If you submit the KOS document with the wrong value for
        DocumentEntry.patientId (including the wrong assigning authority
        or improper coding), the test software will not find your
        submission. The test will fail at that point.<br>
      </li>
    </ul>
    <h3>Data Requirements</h3>
    <h4>KOS Requirements</h4>
    <p>See instructions in ids_2018_4800.<br>
    </p>
    <h4>Metadata Requirements</h4>
    See instructions in ids_2018_4800.
  </body>
</html>
