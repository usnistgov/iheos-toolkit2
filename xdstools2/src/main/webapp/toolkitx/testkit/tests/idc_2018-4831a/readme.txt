Retrieve KOS: Single Image Study
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Imaging Document Consumer Retrieve KOS: Single Image Study</title>
  </head>
  <body>
    <h2>Imaging Document Consumer Retrieve KOS: Single Image Study</h2>
    Tests the ability of the Imaging Document Consumer actor (SUT) to
    retrieve one KOS object from a Repository/Registry for a specific
    single image study.
    <h3>Purpose / Context</h3>
    The Imaging Document Consumer SUT:
    <ul>
      <li>Sends an ITI-18 Registry Stored Query to the
        Repository/Registry simulator to locate the KOS object for a
        specific patient. </li>
      <li>Sends an ITI-43 Retrieve Document Set to the
        Repository/Registry simulator to retrieve the KOS object
        specified for this test.</li>
    </ul>
    The test points are:
    <ul>
      <li>The Imaging Document Consumer can perform a valid ITI-18
        Registry Stored Query. </li>
      <li>The ITI-18 Registry Stored Query contains the Patient
        Identifier specified by this test. </li>
      <li>The Imaging Document Consumer can perform a valid ITI-43
        Retrieve Document Set and retrieve the KOS object created for
        this test. </li>
    </ul>
    <h3>Test Steps</h3>
    Setup:
    <ul>
      <li>Images and KOS objects are pre-loaded into the testing system
        actor simulators. </li>
    </ul>
    Instructions:
    <ol>
      <li>The Imaging Document Consumer is instructed to query the
        Registry/Repository simulator for the KOS object for the patient
        with patient ID:<br>
        IDCAD001-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO </li>
      <li>The Imaging Document Consumer should both query for and
        retrieve the KOS object. If the Imaging Document Consumer
        performs a further RAD-16, RAD-55 or RAD-69 transaction, those
        transactions will be ignored. </li>
    </ol>
    <h3>Verification</h3>
    <ol>
      <li>Test Manager: Locate the stored query sent by the Imaging
        Document Consumer
        <ul>
          <li>Select the proper Environment and Test Session (depends on
            Imaging Document Consumer)</li>
          <li>In the left flap, select Simulator Manager</li>
          <li>In the Simulator Manager panel, select “Transaction
            Log” for the Document Repository/Registry created for this
            Imaging Document Consumer.</li>
          <li>The inspector will show transactions processed by the
            simulator. Select the STORED_QUERY transaction for review.
            You will need to correlate the time stamp in the inspector
            with information from the person testing.</li>
        </ul>
      </li>
      <li>Test Manager: Examine the Log window
        <ul>
          <li>Verify that all test assertions pass.</li>
          <li>Verify that the $XDSDocumentEntryPatientId contains the
            value:
            IDCAD001-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO</li>
        </ul>
      </li>
      <li>Test Manager: Locate the retrieve request to retrieve the KOS
        object.
        <ul>
          <li>Using the same inspector for the same Document
            Repository/Registry, you should see a RETRIEVE request
            captured after one or more STORED_QUERY messages. Select the
            RETRIEVE request for review.</li>
        </ul>
      </li>
      <li>Test Manager: Examine the Log window.
        <ul>
          <li>Verify that all test assertions pass.</li>
        </ul>
      </li>
      <li>Test Manager: Observe in person or use Internet screen sharing
        to verify that the Imaging Document Consumer has indeed
        retrieved the KOS object. Take a screen capture of the evidence.
        <ul>
          <li>Verify that the Patient Identifier inside the KOS object
            is IDC001-a</li>
          <li>Note that the Imaging Document Consumer might not have
            software that renders the KOS object. That system under test
            might take that KOS object and perform another set of
            retrieve functions. You will have to use some judgment in
            determining what is acceptable evidence for this step.</li>
        </ul>
      </li>
      <li>Test Manager: Extra scrutiny tests
        <ul>
          <li>Review the Repository/Registry responses to the initial
            STORED_QUERY requests. Note the values for DocumentUniqueId
            and RepositoryUniqueId.</li>
          <li>Examine the Request Message (window) for the RETRIEVE
            request. Compare the DocumentUniqueId and RepositoryUniqueId
            to the values observed in the stored query responses.</li>
        </ul>
      </li>
    </ol>
  </body>
</html>
