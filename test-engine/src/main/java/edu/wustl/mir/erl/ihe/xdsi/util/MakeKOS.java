package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.logging.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.tool.common.DicomFiles;
import org.dcm4che3.util.UIDUtils;

/**
 * Based on org.dcm4che3.tool.mkkos.MkKOS.
 * Original authors:
 * @author Gunter Zeilinger
 * @author Michael Backhaus
 */
public class MakeKOS {
   
   private static Logger log = Utility.getLog();
   
    private static final int[] PATIENT_AND_STUDY_ATTRS = {
        Tag.SpecificCharacterSet,
        Tag.StudyDate,
        Tag.StudyTime,
        Tag.AccessionNumber,
        Tag.IssuerOfAccessionNumberSequence,
        Tag.ReferringPhysicianName,
        Tag.PatientName,
        Tag.PatientID,
        Tag.IssuerOfPatientID,
        Tag.PatientBirthDate,
        Tag.PatientSex,
        Tag.StudyInstanceUID,
        Tag.StudyID 
    };
    
    private static String submitObjectsRequestMetadataTemplate;
    
    static {
       try {
          URL url = MakeKOS.class.getResource("MakeKOS_SubmitObjectsRequest_Metadata_Template.xml");
          url = MakeKOS.class.getResource("metadata-smm.xml");
          Utility.getLog().fine(url.toString());
          submitObjectsRequestMetadataTemplate = IOUtils.toString(url, "UTF-8");
       } catch (IOException e) {
          log.severe(Utility.getEM(e));
          System.exit(1);
       }
    }
    
    /** 
     * directory containing images to include in KOS document.
     * If null, current working directory is used.
     */
    private String studyDirectory = null; 
    /** 
     * directory where KOS document is to be placed.
     * If null, current working directory is used.
     */
    private String kosDirectory = null;
    /** file name for KOS document file */
    private String kosFileName = "kos.dcm";
    /** file name for KOS SubmitObjectsRequest metadata file */
    private String submitObjectsRequestMetadataFileName = "metadata.xml";

    private final Attributes attrs = new Attributes();
    private String uidSuffix = null;
    private boolean nofmi = false;
    private DicomEncodingOptions encOpts = 
       new DicomEncodingOptions(false, true, false, true, false);
    private String tsuid = UID.ExplicitVRLittleEndian;
    private String seriesNumber = "999";
    private String instanceNumber = "1";
    private String keyObjectDescription = "";
    //private Attributes documentTitle = CID7010.DCM_113000.toCodeItem();
    private Attributes documentTitle = CID7010.DCM_113030.toCodeItem();
    private Attributes documentTitleModifier = null;

    private Attributes kos;
    private Sequence evidenceSeq;
    private Sequence contentSeq;
    
    //private AEBean sendingAE;
    private String retrieveAETitle = null;
    private String retrieveLocationUID = null;
    private String identifierAffinityDomain = null;
    private String departmentalIdentifierQualifier = null;
    
    /**
    * @param sendingAE the {@link #sendingAE} to set
    */
//   public void setSendingAE(AEBean sendingAE) {
//      this.sendingAE = sendingAE;
//   }
    
    public void setRetrieveAETitle(String retrieveAETitle) {
    	this.retrieveAETitle = retrieveAETitle;
    }
    
    public void setRetrieveLocationUID(String retrieveLocationUID) {
    	this.retrieveLocationUID = retrieveLocationUID;
    }
    
    public void setIdentifierAffinityDomain(String identifier) {
    	this.identifierAffinityDomain = identifier;
    }
    
    public void setDepartmentalIdentifierQualifier(String qualifier) {
    	this.departmentalIdentifierQualifier = qualifier;
    }

   /**
     * suffix to be appended to the Study, Series and SOP Instance UID of referenced
     * object(s). Default none.
    * @param uidSuffix to add
    */
   public final void setUIDSuffix(String uidSuffix) {
       this.uidSuffix = uidSuffix;
   }
   
   /**
    * @param studyDirectory complete path to a folder on the file system
    * containing the images for which the KOS document is to be created. Must
    * have read permissions. Nothing in this folder will be modified by the
    * method.
    */
   public void setStudyDirectory(String studyDirectory) {
      this.studyDirectory = studyDirectory;
   }

   /**
    * @param kosDirectory complete path to a folder where the KOS document is to
    * be placed. Must have permissions. Other than creating the KOS document,
    * nothing in this folder will be modified.
    */
   public void setKosDirectory(String kosDirectory) {
      this.kosDirectory = kosDirectory;
   }

   /**
    * @param kosFileName the file name (including any extension) to be given to
    * the created KOS document, in the outputFolder. Any previously existing
    * file with this name will be deleted.
    */
   public void setKosFileName(String kosFileName) {
        this.kosFileName = kosFileName;
    }

   /**
    * @param kosMetadataFileName the {@link #submitObjectsRequestMetadataFileName} to set
    */
   public void setKosMetadataFileName(String kosMetadataFileName) {
      this.submitObjectsRequestMetadataFileName = kosMetadataFileName;
   }

   /**
    * store KOS without File Meta Information with Implicit VR Little Endian.
    * Default create DICOM Part 10 file with File Meta Information.
    * Default false.
    * @param nofmi boolean value to set
    */
   public void setNoFileMetaInformation(boolean nofmi) {
        this.nofmi = nofmi;
    }

    /**
     * DICOM group and sequence encoding options to use. See
     * {@link org.dcm4che3.io.DicomEncodingOptions DicomEncodingOptions} for
     * details. Default is DicomEncodingOptions(false, true, false, true, false)
    * @param encOpts DicomEncodingOptions to use.
    */
   public final void setEncodingOptions(DicomEncodingOptions encOpts) {
        this.encOpts = encOpts;
    }

   /**
    * Transfer Syntax UID. Default, Explicit VR Little Endian for generated
    * DICOM Part 10 file. Other entries are not currently validated. They should
    * be constants taken from the {@link org.dcm4che3.data.UID UID} class.
    * Default is UID.ExplicitVRLittleEndian.
    * 
    * @param tsuid string UID value to set.
    */
   public final void setTransferSyntax(String tsuid) {
        this.tsuid = tsuid;
    }

    /**
     * Set Series Number of created KOS. Default 999.
    * @param seriesNumber to use, as String.
    */
   public final void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    /**
     * Set Instance Number of created KOS. Default 1.
    * @param instanceNumber to use as String.
    */
   public final void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    /**
     * set KOS Document Key Object Description. Default empty string.
    * @param keyObjectDescription String to set
    */
   public final void setKeyObjectDescription(String keyObjectDescription) {
        this.keyObjectDescription = keyObjectDescription;
    }
    
    /**
     * KOS Document title. Must be a value in {@link CID7010}. Default is
     * CID7010.DCM_113000 "Of Interest". To change, used CID7010 helper. For
     * example: <pre>{@code
     *    setDocumentTitle(CIR7010.DCM_113003.toCodeItem()}</pre>
     * @param codeItem 3 element {@link org.dcm4che3.data.Attributes Attributes}
     * set with code value, coding scheme designator and code description for
     * KOS Document Title
     */
    public final void setDocumentTitle(Attributes codeItem) {
        this.documentTitle = codeItem;
    }
    /**
     * KOS Document title modifier. Must be a value in {@link CID7010}. Default 
     * is null. To change, used CID7010 helper. For example: <pre>{@code
     *    setDocumentTitle(CIR7010.DCM_113003.toCodeItem()}</pre>
     * @param codeItem 3 element {@link org.dcm4che3.data.Attributes Attributes}
     * set with code value, coding scheme designator and code description for
     * KOS Document Title Modifier
     */
    public final void setDocumentTitleModifier(Attributes codeItem) {
        this.documentTitleModifier = codeItem;
    }
    
   /**
    * Creates KOS Document matching parameter settings.
    * 
    * @throws Exception on error, for example, IO, invalid file or directory
    * names, parsing errors.
    */
   public void makeKos() throws Exception {
       Utility.invoked();
       Path studyDirPath = Utility.getRunDirectoryPath().resolve(studyDirectory);
       Utility.isValidPfn("study directory", studyDirPath, PfnType.DIRECTORY, "r");
       Path kosDirPath = Utility.getRunDirectoryPath().resolve(kosDirectory);
       Files.createDirectories(kosDirPath);
       Utility.isValidPfn("kos directory", kosDirPath, PfnType.DIRECTORY, "rwx");
       Path kosFilePath = kosDirPath.resolve(kosFileName.trim());
       Files.deleteIfExists(kosFilePath);
       Files.deleteIfExists(kosDirPath.resolve(submitObjectsRequestMetadataFileName));
       List<File> imgFiles = 
          (List<File>) FileUtils.listFiles(studyDirPath.toFile(), 
          TrueFileFilter.TRUE, TrueFileFilter.TRUE);
       List<String> imgFileNames = new ArrayList<String>();
       for (File imgFile : imgFiles) 
          imgFileNames.add(imgFile.getAbsolutePath());
       log.fine("Scanning DICOM files");
       DicomFiles.scan(imgFileNames, new DicomFiles.Callback() {
          @Override
         public boolean dicomFile(File f, Attributes fmi,
             long dsPos, Attributes ds) {
             return addInstance(ds);
          }
       });
       DicomOutputStream dos = new DicomOutputStream(
          new BufferedOutputStream(new FileOutputStream(kosFilePath.toString())),
          nofmi ? UID.ImplicitVRLittleEndian
             : UID.ExplicitVRLittleEndian);
       dos.setEncodingOptions(encOpts);
       try {
          dos.writeDataset(
             nofmi ? null : kos.createFileMetaInformation(tsuid),
                kos);
       } finally {
          dos.close();
       }
       createSubmitObjectsRequestMetadata(departmentalIdentifierQualifier, identifierAffinityDomain);
    }
    
    private boolean addInstance(Attributes inst) {
        CLIUtils.updateAttributes(inst, attrs, uidSuffix);
        String studyIUID = inst.getString(Tag.StudyInstanceUID);
        String seriesIUID = inst.getString(Tag.SeriesInstanceUID);
        String iuid = inst.getString(Tag.SOPInstanceUID);
        String cuid = inst.getString(Tag.SOPClassUID);
        if (studyIUID == null || seriesIUID == null || iuid == null || cuid == null)
            return false;
        if (kos == null)
            kos = createKOS(inst);
        refSOPSeq(studyIUID, seriesIUID, retrieveAETitle).add(refSOP(cuid, iuid));
        contentSeq.add(contentItem(valueTypeOf(inst), refSOP(cuid, iuid)));
        return true;
    }

    private Sequence refSOPSeq(String studyIUID, String seriesIUID, String retrieveAE) {
        Attributes refStudy = getOrAddItem(evidenceSeq, Tag.StudyInstanceUID, studyIUID);
        Sequence refSeriesSeq = refStudy.ensureSequence(Tag.ReferencedSeriesSequence, 10);
        Attributes refSeries = getOrAddItem(refSeriesSeq,Tag.SeriesInstanceUID, seriesIUID);
        refSeries.setString(Tag.RetrieveAETitle, VR.AE, retrieveAE);
        refSeries.setString(Tag.RetrieveLocationUID, VR.UI, retrieveLocationUID);
        return refSeries.ensureSequence(Tag.ReferencedSOPSequence, 100);
    }

    private Attributes getOrAddItem(Sequence seq, int tag, String value) {
        for (Attributes item : seq)
            if (value.equals(item.getString(tag)))
                return item;
        
        Attributes item = new Attributes(2);
        item.setString(tag, VR.UI, value);
        seq.add(item);
        return item;
    }

    private String valueTypeOf(Attributes inst) {
        return inst.contains(Tag.PhotometricInterpretation) ? "IMAGE"
                      : inst.contains(Tag.WaveformSequence) ? "WAVEFORM"
                                                            : "COMPOSITE";
    }

    private Attributes refSOP(String cuid, String iuid) {
        Attributes item = new Attributes(2);
        item.setString(Tag.ReferencedSOPClassUID, VR.UI, cuid);
        item.setString(Tag.ReferencedSOPInstanceUID, VR.UI, iuid);
        return item;
    }
    
   private void createSubmitObjectsRequestMetadata(String departmentalQualifier, String patientIdentifierAffinityDomain) throws Exception {
	  log.fine("MakeKOS::createSubmitObjectsRequestMetadata enter method");
	  log.fine(" Patient Identifier Affinity Domain: " + patientIdentifierAffinityDomain);
	  log.fine(" PID Affinity Domain, XML escaped:   " + StringEscapeUtils.escapeXml(patientIdentifierAffinityDomain));
	  String patientIdentifierDepartment = kos.getString(Tag.PatientID) + departmentalQualifier;
	  log.fine(" Patient Identifier Department: " + patientIdentifierDepartment);
	  log.fine(" PID Dept, XML escaped:         " + StringEscapeUtils.escapeXml(patientIdentifierDepartment));
      String now = new SimpleDateFormat("yyyyMMddHHmmss").format((new Date()));
      File submitObjectsRequestMetadataFile =
         Utility.getRunDirectoryPath().resolve(kosDirectory).resolve(submitObjectsRequestMetadataFileName).toFile();
      HL7V2HierarchicDesignatorMacro accessionIssuer =
         new HL7V2HierarchicDesignatorMacro(kos.getSequence(Tag.IssuerOfAccessionNumberSequence));
      new Plug(submitObjectsRequestMetadataTemplate)
         .set("now", now)
         .set("pid", StringEscapeUtils.escapeXml(patientIdentifierAffinityDomain))
         .set("pidDepartment", StringEscapeUtils.escapeXml(patientIdentifierDepartment))
         .set("documentID", kos.getString(Tag.SOPInstanceUID))
//         .set("accession", kos.getString(Tag.AccessionNumber))
//         .set("Accession​IssuerUID", accessionIssuer.getId())
//         .set("Accession​IssuerIDType", accessionIssuer.getType())
         .set("pname", kos.getString(Tag.PatientName, "DOE^JOHN"))
         .set("dob", kos.getString(Tag.PatientBirthDate, "19640101"))
         .set("sex", kos.getString(Tag.PatientSex, "M"))
//         .set("sourceAeOid", sendingAE.getOid())
//         .set("submissionSetOid", Identifiers.generateUniqueOID())
//         .set("uid", Identifiers.generateUniqueOID())
         .get(submitObjectsRequestMetadataFile, false);
      log.fine("MakeKOS::createSubmitObjectsRequestMetadata exit method");
   }

    private Attributes createKOS(Attributes inst) {
        Attributes atrs = new Attributes(inst, PATIENT_AND_STUDY_ATTRS);
        atrs.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
        atrs.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
        atrs.setDate(Tag.ContentDateAndTime, new Date());
        atrs.setString(Tag.Modality, VR.CS, "KO");
        atrs.setString(Tag.Manufacturer, VR.LO, "WUSTL");
        atrs.setNull(Tag.ReferencedPerformedProcedureStepSequence, VR.SQ);
        atrs.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
        atrs.setString(Tag.SeriesNumber, VR.IS, seriesNumber);
        atrs.setString(Tag.InstanceNumber, VR.IS, instanceNumber);
        atrs.setString(Tag.ValueType, VR.CS, "CONTAINER");
        atrs.setString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");
        atrs.newSequence(Tag.ConceptNameCodeSequence, 1).add(documentTitle);
        evidenceSeq = atrs.newSequence(Tag.CurrentRequestedProcedureEvidenceSequence, 1);
        atrs.newSequence(Tag.ContentTemplateSequence, 1).add(templateIdentifier());
        contentSeq = atrs.newSequence(Tag.ContentSequence, 1);
        if (documentTitleModifier != null)
            contentSeq.add(documentTitleModifier());
        if (keyObjectDescription != null)
            contentSeq.add(keyObjectDescription());
        return atrs;
    }

    private Attributes templateIdentifier() {
        Attributes atrs = new Attributes(2);
        atrs.setString(Tag.MappingResource, VR.CS, "DCMR");
        atrs.setString(Tag.TemplateIdentifier, VR.CS, "2010");
        return atrs ;
    }

    private Attributes documentTitleModifier() {
        Attributes item = new Attributes(4);
        item.setString(Tag.RelationshipType, VR.CS, "HAS CONCEPT MOD");
        item.setString(Tag.ValueType, VR.CS, "CODE");
        //item.newSequence(Tag.ConceptNameCodeSequence, 1).add(CID7010.DCM_113000.toCodeItem());
        item.newSequence(Tag.ConceptNameCodeSequence, 1).add(CID7010.DCM_113030.toCodeItem());
        item.newSequence(Tag.ConceptCodeSequence, 1).add(documentTitleModifier);
        return item;
    }

    private Attributes keyObjectDescription() {
        Attributes item = new Attributes(4);
        item.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
        item.setString(Tag.ValueType, VR.CS, "TEXT");
        item.newSequence(Tag.ConceptNameCodeSequence, 1)
           .add(DICOMUtility.makeCodedItem("113030", "DCM", "Manifest"));
        //item.setString(Tag.TextValue, VR.UT, keyObjectDescription);
        item.setString(Tag.TextValue, VR.UT, "Manifest");
        return item;
    }

    private Attributes contentItem(String valueType, Attributes refSOP) {
        Attributes item = new Attributes(3);
        item.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
        item.setString(Tag.ValueType, VR.CS, valueType);
        item.newSequence(Tag.ReferencedSOPSequence, 1).add(refSOP);
        return item;
    }
 }