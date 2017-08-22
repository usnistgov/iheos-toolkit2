Imaging Document Source: Read This First
<h2>Imaging Document Source: Read This First</h2>

<p>The System Under Test (SUT) is an Imaging Document Source.</p>

<p>You need to configure your Imaging Document Source to communicate with the
   simulators listed in the conformance testing tool.
   If you are reading this document as a standalone document, that configuration
   is not available to you.
   If you are reading this document in the context of the XDS Toolkit, it is better
   to close this test and read the introductory material in the toolkit for testing
   the Imaging Document Source.
   That introductory text includes configuration parameters and diagrams.
</p>

<p>Use the following value for the Assigning Authority for the patient identifiers
   in the XDS Affinity Domain:
   <br /><blockquote>1.3.6.1.4.1.21367.2005.13.20.1000</blockquote>
   Patient identifiers will be of the following form (ignoring escaping for XML)

   <blockquote>IDS-AD001-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO</blockquote>
</p>

<p>The Imaging Document Source is required to use the following OID in the
   repositoryUniqueID for RAD-69 transactions and in the
   Retrieve Location UID (0040, E011) element in the KOS object:
   <br /><blockquote>1.3.6.1.4.1.21367.13.80.110</blockquote>
   No restrictions are placed on the element Retrieve AE Title (0008, 0054)
</p>

<p>The tests for an Imaging Document Source use a fixed set of images as input data.
   Each imaging study is identified by a department identifier
   (DICOM Patient ID (0010,0020) and possibly by an Accession Number (0008, 0050).
   The Patient Identifier in the XDS.b metadata is not the same as the identifier
   in the DICOM image.
   It is the responsibility of the Imaging Document Source to map the
   departmental identifier to the Affinity Domain identifier specified in the test cases.
   The Imaging Document Source only needs to use the Affinity Domain identifier
   in the XDS.b metadata.
   The Imaging Document Source is not required to change the DICOM Patient ID
   (0010,0020) in the DICOM KOS object itself.
   Do not change the Patient ID inside the DICOM KOS object unless your software
   naturally does that.
</p>

<p>The tests below assume a standard testing procedure:

<ol>
 <li>Imaging Document Source imports the test images and does not change patient names,
     patient identifiers, accession numbers or unique identifiers.
     The tests will fail if the Imaging Document Source modifies those elements
     within the images.</li>
 <li>The Imaging Document Source maps the departmental identifiers to the identifiers
     identified by the Affinity Domain (see individual tests for values).
     The test tools do not provide a mapping service.</li>
 <li>The Imaging Document Source generates a KOS object for each imaging study
     and submits that KOS object via a Provide and Register transaction to a
     Repository/Registry simulator that is dedicated to the Imaging Document Source.
  <ul><li>Note that there is at least one patient that has three imaging studies.
          The tests assume that each imaging study is processed separately by
          the Imaging Document Source.
          That is, the tests expect separate KOS objects for each imaging study,
          even though the imaging studies are for the same patient.</li></ul></li>
 <li>The Imaging Document Source provides access to each imaging study using all
     three mechanisms defined by the XDS-I profile:
   <ul><li>RAD-69 Retrieve Imaging Document Set</li>
       <li>RAD-55 DICOM WADO Retrieve</li>
       <li>RAD-16 DICOM C-Move</li></ul>
 </li>
</ol>
