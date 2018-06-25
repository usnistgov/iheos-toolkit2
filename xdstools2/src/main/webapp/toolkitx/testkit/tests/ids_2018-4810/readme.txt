RAD-55: WADO Exception Cases
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Source: RAD 55: WADO Retrieve, Exception
      Cases</title>
  </head>
  <body>
    <h2>RAD 55: WADO Retrieve, Exception Cases</h2>
    <h3>Purpose / Context</h3>
    This is a test of a number of exception cases related to the WADO
    Retrieve (RAD-55) transaction.
    <ol>
      <li>Study Instance UID and Series Instance UID reference known
        objects; SOP Instance UID does not reference a known object. </li>
      <li>Study Instance UID and Series Instance UID reference known
        objects; SOP Instance UID references an object in a different
        series in the same study. </li>
      <li>Study Instance UID and Series Instance UID reference known
        objects; SOP Instance UID references an object in a different
        study. </li>
      <li>Series Instance UID and SOP Instance UIDs reference known
        objects; Study Instance UID is included but empty. </li>
      <li>Study Instance UID and SOP Instance UIDs reference known
        objects; Series Instance UID is included but empty. </li>
      <li>SOP Instance UID references a known object; Study Instance UID
        and Series Instance UID are included but empty. </li>
    </ol>
    <h3>Prior to running this test:</h3>
    Complete the setup steps listed in ids_2018-4801a and
    ids_2018-4801b. That is, import the imaging data for these tests and
    submit the KOS object to the assigned Document Repository simulator.<br>
  </body>
</html>
