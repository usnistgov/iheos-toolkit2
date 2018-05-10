The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:

<ul>
<li>SOAP Header = MP: MA Default Request (TestTool) Message Parameters</li>
<li>$XDSDocumentEntryPatientID = [P-000000012 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>returnType = objectRef</li>
<li>SOAP request = synchronous</li>
</ul>

<b>Expected Result</b>: The System successfully processes the Request and returns a QD Response to the Testing Tool that contains: An empty list
