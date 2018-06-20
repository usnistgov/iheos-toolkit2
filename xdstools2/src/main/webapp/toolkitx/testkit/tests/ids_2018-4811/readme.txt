RAD-69: SOAP Exception Cases
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Source: RAD-69: Retrieve Image Document Set,
      Exception Cases</title>
  </head>
  <body>
    <h2>RAD-69: Retrieve Image Document Set, Exception Cases</h2>
    <p>Tests the ability of the Image Document Source actor (SUT) to
      respond correctly to a Retrieve Image Document Set (RAD-69)
      Request from an Image Document Consumer actor (Simulator) which
      contains errors.</p>
    <p>This is the list of individual cases that are run:<br>
    </p>
    <ul>
      <li>case-0_Comparison_Transaction</li>
      <li>case-1_Invalid_RepositoryUID</li>
      <li>case-2_Unknown_SOP_Instance_UID</li>
      <li>case-3_SOP_Instance_UID_not_in_requested_series</li>
      <li>case-4_SOP_Instance_UID_not_in_requested_study</li>
      <li>case-5_Empty_Study_Instance_UID</li>
      <li>case-6_Empty_Series_Instance_UID</li>
      <li>case-7_Empty_Study_and_Series_Instance_UIDs</li>
      <li>case-8_Empty_Transfer_Syntax_UID</li>
      <li>case-9_Invalid_Transfer_Syntax_UID</li>
    </ul>
    <p> </p>
    <p>The first retrieve request (Case 0) is actually a legal request
      intended to ensure that communication with the Imaging Document
      Source under test is working. All other retrieve requests contain
      configuration or data errors or have missing data.</p>
    <p></p>
    <h3>Prior to running this test:</h3>
    <p> Complete the setup steps listed in ids_2018-4801a and
      ids_2018-4801b. That is, import the imaging data for these tests
      and submit the KOS object to the assigned Document Repository
      simulator. </p>
  </body>
</html>
