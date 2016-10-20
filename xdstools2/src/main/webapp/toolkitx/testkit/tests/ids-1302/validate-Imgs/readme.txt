<h3>Validate DICOM objects retrieved from IDS SUT.</h2>

<p/>When the test section is run, the testkit will evaluate the DICOM images sent
in the RAD-69 Response received from the IDS SUT during the retrieve section of 
the test, comparing them with 'gold standard' images in the image cache. 
Validations include:
<ol>
<li/>That the images are valid DICOM object files.
<li/>That the values of the DICOM Tags listed in the TagList Element of the 
testplan, are the same in the received and 'gold standard' images.
</ol>
The other elements of the SOAP Response are validated in the Validate-R section.