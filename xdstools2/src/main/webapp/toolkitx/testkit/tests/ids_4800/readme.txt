IDS_4800: Read Me First
<h2>Read Me First: Imaging Document Source Tests</h2>

<p>This Read Me First test repeats information that is included in the Preamble/Overview area of the XDS Toolkit for Imaging Document Source tests.
 It is included as a separate test to support external documentation requirements.
 If you have direct access to the XDS Toolkit for testing an Imaging Document Source actor in the XDS-I.b profile, you can ignore this content.
 If you are reading test descriptions without that access (through an external web site), this information will be useful.</p>

<p>
 Please read the overview material here:
 <a href="https://github.com/usnistgov/iheos-toolkit2/wiki/Conformance-XDSI-Imaging-Document-Source-DICOM-Instances">Conformance XDSI Imaging Document Source DICOM Instances</a>
 for information about where to find the test data and for general execution instructions.
</p>

<p>
 <ul>
  <li>Use the following value for the Assigning Authority for the patient identifiers in the XDS Affinity Domain: 1.3.6.1.4.1.21367.2005.13.20.1000</li>
  <li>Patient identifiers will be of the following form (ignoring escaping for XML): IDS-AD001-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</li>
  <li>Imaging Document Source is required to use the following OID in the repositoryUniqueID for RAD-69 transactions and in the Retrieve Location UID (0040, E011) element in the KOS object: 1.3.6.1.4.1.21367.13.80.110</li>
  <li>No restrictions are placed on the element Retrieve AE Title (0008, 0054)</li>
 </ul>
</p>

<p>
 The tests for an Imaging Document Consumer use a fixed set of images as input data.
 Each imaging study is identified by a department identifier (DICOM Patient ID (0010, 0020) and possibly by an Accession Number (0008, 0050). The Patient Identifier in the XDS.b metadata is not the same as the identifier in the DICOM image. It is the responsibility of the Imaging Document Source to map the departmental identifier to the Affinity Domain identifier specified in the test cases.
</p>

<p>
 The tests below assume a standard testing procedure:
<br />
 Imaging Document Source imports the test images and does not change patient names, patient identifiers, accession numbers or unique identifiers.
 The tests will fail if the Imaging Document Source modifies those elements within the images.
 The Imaging Document Source maps the departmental identifiers to the identifiers identified by the Affinity Domain (see individual tests for values).
 The test tools do not provide a mapping service.
 Imaging Document Source generates a KOS object for each imaging study and submits that KOS object via a Provide and Register transaction to a Repository/Registry simulator that is dedicated to the Imaging Document Source.
 Note that there is at least one patient that has three imaging studies. The tests assume that each imaging study is processed separately by the Imaging Document Source.
 That is, the tests expect separate KOS objects for each imaging study, even though the imaging studies are for the same patient.
 Imaging Document Source provides access to each imaging study using two mechanisms defined by the XDS-I profile:
 <ul>
  <li>RAD-69 Retrieve Imaging Document Set</li>
  <li>RAD-55 DICOM WADO Retrieve</li>
 </ul>
 These tests do not cover the DICOM C-Move feature of the XDS-I profile.
</p>
