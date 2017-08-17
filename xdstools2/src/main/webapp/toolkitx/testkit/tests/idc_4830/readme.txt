Imaging Document Consumer: Read This First
<h2>Imaging Document Consumer: Read This First</h2>

<p>The System Under Test (SUT) is an Imaging Document Consumer.</p>

<p>The tests for an Imaging Document Consumer use a fixed set of images as input data.
   The images with patient names and identifiers are listed with each test as appropriate.
</p>


<p>The test data has departmental patient identifiers (e.g., those used in the
   Radiology Department when the images are acquired) and identifiers for the
   Affinity Domain.
   There is no assigning authority for the departmental identifiers.
   The assigning authority for the Affinity Domain is:
   <ul><li>&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</li></ul>

<h3>Standard Test Procedure</h3>
<p>The tests below assume a standard testing procedure:</p>

<ol>
 <li>A standard test set is created that contains imaging data and associated KOS objects.
     The standard test images are identified by patient identifier and are listed with
     each test as appropriate.</li>
 <li>The test software does not provide a mapping mechanism between the patient
     identifier in the imageand the patient identifier in the Affinity Domain.
     It is the responsibility of the Imaging Document Consumer to use the correct
     patient identifier for the Affinity Domain.</li>
 <li>The Imaging Document Consumer is instructed to send query and retrieve requests
     to the testing system.
     The logging mechanism of the simulators in the testing system records the requests.
     The testing system supports retrieves using the RAD-55 (WADO) and
     RAD-69 (SOAP) transactions.
     Traditional DICOM C-Move transactions are not supported.
     The Imaging Document Consumer under test needs to complete all tests using
     RAD-55 transactions or all tests using RAD-69 transactions.
     The Imaging Document Consumer may test both RAD-55 and RAD-69 transactions if
     both are supported.</li>
 <li>The test manager reviews the Imaging Document Consumer requests captured
     by the test system simulators.</li>
</ol>

<p>Test validation has several aspects:</p>
<ol>
 <li>Did the Imaging Document Consumer use properly formatted XDS.b
     query/retrieve operations (ITI-43, RAD-69 transactions)?</li>
 <li>Did the Imaging Document Consumer use properly formatted DICOM WADO
     retrieve operations (RAD-55 transaction)?</li>
 <li>Did the Imaging Document Consumer use the proper patient identifiers
     (and other identifiers) during the retrieve process.</li>
 <li>Can the Imaging Document Consumer demonstrate that it can use the retrieved
     images in their product?
     This step requires some interpretation.
     Some systems are workstations where the end product is to render data for a customer.
     Other Imaging Document Consumer systems might consist of a middleware
     implementation that retrieves the data and passes it on to another application.</li>
</ol>
