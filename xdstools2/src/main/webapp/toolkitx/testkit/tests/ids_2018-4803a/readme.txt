RAD-69: SOAP Retrieve for Single Image Study
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Source: RAD-69: SOAP Retrieve for Single
      Image Study</title>
  </head>
  <body>
    <h2>RAD-69: SOAP Retrieve for Single Image Study</h2>
    <h3>Purpose / Context</h3>
    The Imaging Document Source has submitted a KOS for a study with a
    single image. In this test, we retrieve the single image using a
    SOAP&nbsp; retrieve.
    <h3>Instructions</h3>
    <ol>
      <li> Complete the instructions listed in test ids_2018-4801a. This
        will register the proper KOS object with the testing system.
        Assuming test ids_2018-4801a has completed successfully, you do
        not need to submit the data a second time.</li>
      <li>Make sure that the image data for patient
        IDS_2018-4801a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO
        is in your system and ready for a WADO retrieve.</li>
      <li>Activate the test button to initiate the SOAP retrieve and
        validation steps.</li>
    </ol>
    <h3>Notes</h3>
    <p><br>
    </p>
    <h3>Validation</h3>
    <p>The test software provides the following validation tests:<br>
    </p>
    <ol>
      <li>SOAP response is properly formatted.</li>
      <li>SOAP response contains proper content</li>
      <ul>
        <li>Repository Unique Id</li>
        <li>Document Unique ID (DICOM SOP Instance UID)<br>
        </li>
      </ul>
      <li>DICOM image is returned in SOAP response<br>
      </li>
      <li>Original DICOM values in image remain unchanged</li>
      <ol>
        <li>Patient ID, Birth Date, Sex</li>
        <li>Study Instance UID</li>
        <li>Series Intance UID</li>
        <li>SOP Instance UID<br>
        </li>
      </ol>
    </ol>
  </body>
</html>
