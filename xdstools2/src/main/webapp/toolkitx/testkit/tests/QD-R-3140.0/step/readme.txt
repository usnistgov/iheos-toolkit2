The Testing Tool sends a synchronous Find Documents Request to the System, using the following required parameters:
<ul>
<li>SOAP Header = MP: MA Default Request (SUT) Message Parameters</li>
<li>$XDSDocumentEntryPatientID= [P-000000031 PID]</li>
<li>$XDSDocumentEntryStatus = Approved OR Deprecated</li>
<li>$XDSDocumentEntryClassCode = [code]^^[scheme] class code = 34133-9 class code scheme = 2.16.840.1.113883.6.1  OR $XDSDocumentEntryClassCode = [code]^^[scheme] class code = 28634-4 class code scheme = 2.16.840.1.113883.6.1</li>
<li>$XDSDocumentEntryServiceStartTimeFrom = 20070315</li>
<li>$XDSDocumentEntryServiceStartTimeTo = 20070415</li>
<li>$XDSDocumentEntryServiceStopTimeFrom = 20070401</li>
<li>$XDSDocumentEntryServiceStopTimeTo = 20070415</li>
<li>$XDSDocumentEntryCreationTimeFrom =20090513</li>
<li>$XDSDocumentEntryCreationTimeTo = 20090517</li>
<li>$XDSDocumentEntryPracticeSettingCode = [code]^^[scheme] practice setting code = 408478003 practice setting code scheme = 2.16.840.1.113883.6.96 OR $XDSDocumentEntryPracticeSettingCode = [code]^^[scheme]  practice setting code = 394581000 practice setting code scheme = 2.16.840.1.113883.6.96</li>
<li>$XDSDocumentEntryHealthcareFacilityTypeCode = [code]^^[scheme] healthcare facility type code = 36125001 healthcare facility type code scheme = 2.16.840.1.113883.6.96  OR $XDSDocumentEntryHealthcareFacilityTypeCode = [code]^^[scheme] healthcare facility type code = 73770003 healthcare facility type code scheme = 2.16.840.1.113883.6.96</li>
<li>$XDSDocumentEntryEventCodeList = [code]^^[scheme] event code list code = T-32000 event code list code scheme = SNM3  OR $XDSDocumentEntryEventCodeList = [code]^^[scheme]  event code list code = T-32001 event code item scheme = SNM3</li>
<li>$XDSDocumentEntryFormatCode = [code]^^[scheme]  format code = urn:ihe:pcc:edr:2007 format code scheme = 2.16.840.1.113883.3.88.12.80.73  OR $XDSDocumentEntryFormatCode = [code]^^[scheme] format code = urn:ihe:pcc:xphr:2007 format code scheme = 2.16.840.1.113883.3.88.12.80.73</li>
<li>$XDSDocumentEntryTypeCode = [code]^^[scheme]  type code = 28619-5 type code scheme = 2.16.840.1.113883.6.1  OR $XDSDocumentEntryTypeCode = [code]^^[scheme]  type code = 11486-8 type code scheme = 2.16.840.1.113883.6.1</li>
<li>$XDSDocumentEntryConfidentialityCode = [code]^^[scheme] confidentiality code = N confidentiality code scheme = 2.16.840.1.113883.5.25  OR $XDSDocumentEntryConfidentialityCode = [code]^^[scheme] confidentiality code = ETH confidentiality code scheme = 2.16.840.1.113883.5.25 returnType = LeafClass</li>
<li>SOAP request = synchronous returnComposedObjects = true</li>
</ul>

<b>Expected Results</b>: The Testing Tool successfully processes the Request and returns a QD Response to the System that contains the following objects:
<ul>
One document with:
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.1 OR (XDSDocumentEntry.patientID = [P-000000031 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000031.1])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.1]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.1]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.2 OR (XDSDocumentEntry.patientID = [P-000000031 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000031.2])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.2]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.2]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.6 OR (XDSDocumentEntry.patientID = [P-000000031 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000031.6])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.6]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.6]</li>
</ul>

Another document with:
</ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.10 OR (XDSDocumentEntry.patientID = [P-000000031 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000031.10])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.10]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.1]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.12 OR  (XDSDocumentEntry.patientID = [P-000000031 PID] AND XDSDocumentEntry.authorPerson = [value from D-000000031.12])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.12]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.12]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.14 OR (XDSDocumentEntry.patientID = [P-000000031 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000031.14])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.14]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.14]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.16 OR (XDSDocumentEntry.patientID = [P-000000031 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000031.16])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.16]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.16]</li>
</ul>

Another document with:
<ul>
<li>A 'DocumentMatch' of either: XDSDocumentEntry.uniqueId = D-000000031.18 OR (XDSDocumentEntry.patientID = [P-000000031 PID]
AND XDSDocumentEntry.authorPerson = [value from D-000000031.18])</li>
<li>A match on: XDSDocumentEntry.status = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.classCode = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.serviceStartTime = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.serviceStopTime = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.creationTime = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.practiceSettingCode = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.healthcareFacilityTypeCode = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.eventCodeList = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.formatCode = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.typeCode = [value from D-000000031.18]</li>
<li>A match on: XDSDocumentEntry.confidentialityCode = [value from D-000000031.18]</li>
</ul>

NOTE: The parameters that are part of the Request should be the minimum that's checked on the Response. The HHMMSS section of the Document Entry Service Times and Document Entry Creation Times.
