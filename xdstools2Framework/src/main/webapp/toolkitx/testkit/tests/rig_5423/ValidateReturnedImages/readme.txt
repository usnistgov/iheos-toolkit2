<h3>Validate DICOM object retrieved from IIG SUT.</h2>

<p/>When the test section is run, the testkit will evaluate the DICOM image sent
in the RAD-75 Response received from the RIG SUT during the retrieve section of 
the test, comparing it with a 'gold standard' image in the image cache. 
Validations include:
<ul>
<li/>That the image is a valid DICOM object file.
<li/>That the values of the DICOM Tags listed in the TagList Element of the 
testplan are the same in the received and 'gold standard' images.
</ul>