The System sends a synchronous/immediate ACP Find Documents Request to the Testing Tool, using the following required parameters:

SOAP Header = MP: MA Default Request (SUT)

Message Parameters

<ul>
<li>$XDSDocumentEntryPatientID = [P-000000002 Resource ID]</li>
<li>$XDSDocumentEntryStatus = Approved</li>
<li>$XDSDocumentEntryClassCode = LOINC code of 57016-8 with scheme "2.16.840.1.113883.6.1”</li>
<li>$XDSDocumentEntryEventCodeList = [Instance Access Policy OID from PD Request with scheme “N/A”]</li>
<li>returnType = LeafClass SOAP</li>
<li>request = synchronous</li>
<li>returnComposedObjects = true</li>
<li>SSA confirmed ACP documents are always Stable.</li>
</ul>
We will allow a participant to request something other than Stable. For 2010 and 2011 the System Under Test can request Approved only or Approved + DeferredCreation. For 2011 the System Under Test can request Stable only or Stable + On Demand