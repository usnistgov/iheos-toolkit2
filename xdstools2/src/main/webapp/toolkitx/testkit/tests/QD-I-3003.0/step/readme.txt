The System sends a synchronous Find Documents Request to the Testing Tool, using the following parameters and class code as many as the System has the ability to send:

<ul>
<li>SOAP Header = MP: MA Default Request (SUT) Message Parameters</li>
<li>$XDSDocumentEntryPatientID= [P-000000031 PID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryClassCode = [code]^^[scheme] class code = 34133-9 class code scheme = 2.16.840.1.113883.6.1</li>
<li>$XDSDocumentEntryTypeCode = [code]^^[scheme] type code = 34133-9 type code scheme = 2.16.840.1.113883.6.1</li>
<li>$XDSDocumentEntryServiceStartTimeFrom = 20070315</li>
<li>$XDSDocumentEntryServiceStartTimeTo = 20070415</li>
<li>$XDSDocumentEntryServiceStopTimeFrom = 20070401</li>
<li>$XDSDocumentEntryServiceStopTimeTo = 20070415</li>
<li>$XDSDocumentEntryCreationTimeFrom =20090513</li>
<li>$XDSDocumentEntryCreationTimeTo = 20090517</li>
<li>$XDSDocumentEntryPracticeSettingCode = [code]^^[scheme] practice setting code = 408478003 practice setting code scheme = 2.16.840.1.113883.6.96</li>
<li>$XDSDocumentEntryHealthcareFacilityTypeCode = [code]^^[scheme] healthcare facility type code = 36125001 healthcare facility type code scheme = 2.16.840.1.113883.6.96</li>
<li>$XDSDocumentEntryEventCodeList = [code]^^[scheme] event code list item= T-32000 event code item scheme = SNM3</li>
<li>$XDDocumentEntryConfidentialityCode = [code]^^[scheme] confidentiality code = N confidentiality code scheme = 2.16.840.1.113883.5.25</li>
<li>$XDSDocumentEntryFormatCode = [code]^^[scheme] format code = urn:ihe:pcc:edr:2007 format code scheme = 2.16.840.1.113883.3.88.12.80.73</li>
<li>$XDSDocumentEntryAuthorPerson = ['%H_nt%']</li>
<li>$XDSDocumentType = [On-Demand OR Stable] *NOTE: Don't use this parameter for 2010 Request</li>
<li>returnType = LeafClass</li>
<li>SOAP request = synchronous</li>
</ul>
NOTE: The System may send fewer than the total number of parameters.

<b>Expected Results</b>: The Testing Tool successfully processes the Request and returns a QD Response to the System that contains the following objects:

A 'Document Match' for  D-000000031.1, D-000000031.2, D-000000031.4, D-000000031.6, D-000000031. 10, D-000000031.12, D-000000031.14, D-000000031.16, D-000000031.18

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Service Times and Document Entry Creation Times  should not be part of that check.
