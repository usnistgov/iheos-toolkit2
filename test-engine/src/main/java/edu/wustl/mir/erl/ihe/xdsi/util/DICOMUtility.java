/*******************************************************************************
 * Copyright (c) 2015 Washington University in St. Louis All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. The License is available at:
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. Contributors:
 * Initial author: Steve Moore/ MIR WUSM IHE Development Project
 * smoore@wustl.edu
 *******************************************************************************/

package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.util.logging.Logger;
import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;

/**
 * Static utility methods used with dcm4che3 DICOM processing.
 */
public class DICOMUtility {

   /**
    * The system newline character. Usually '\n'
    */
   public static final String nl = System.getProperty("line.separator");
   
   /**
    * The system file separator character, used to separate directories in a
    * path. In Windows '\'; in Linux '/'.
    */
   public static final String fs = System.getProperty("file.separator");
   
   private static Logger log = Logger.getLogger(DICOMUtility.class.getName());

   /**
    * Generates a DICOM Key Object Selection (KOS) document for the dicom images
    * in the inputFolder
    * 
    * @param inputFolder complete path to a folder on the file system containing
    * the images for which the KOS document is to be created. Must have read
    * permissions. Nothing in this folder will be modified by the method.
    * @param outputFolder complete path to a folder where the KOS document and
    * metadata.xml are to be placed. Must have permissions. Other than creating
    * the KOS document and metadata.xml, nothing in this folder will be modified.
    * 
    * <p/>
    * <b>Note:</b> Folder names may be given as absolute paths or relative
    * paths, which will be interpreted in relation to the XDSI Root directory.
    * 
    * @param outputName the file name (including any extension) to be given to
    * the created KOS document, in the outputFolder. Any previously existing
    * file with this name will be deleted. 
    * @param sendingAE Name of the sending AE, used for generating the
    * SubmitObjectsRequest metadata creation. Matched against a .ini file in the
    * runDirectory. For example, "SourceAE" would use configuration data from
    * .../runDirectory/SourceAE.ini.
    */
   public static void generateKOS(String inputFolder, String outputFolder, String outputName,
		   String sendingAE, String qualifierDepartmentalIdentifier, String identifierAffinityDomain,
		   String retrieveLocationUID) {
      try {
         log.info("BO generateKOS");
         outputFolder = Utility.getXDSIRootPath().resolve(outputFolder).toString();
         inputFolder = Utility.getXDSIRootPath().resolve(inputFolder).toString();
         MakeKOS makeKos = new MakeKOS();
         makeKos.setStudyDirectory(inputFolder);
         makeKos.setKosDirectory(outputFolder);
         makeKos.setKosFileName(outputName);
         //makeKos.setSendingAE(AEBean.loadFromConfigurationFile(sendingAE));
         makeKos.setRetrieveAETitle(sendingAE);
         makeKos.setRetrieveLocationUID(retrieveLocationUID);
         makeKos.setDepartmentalIdentifierQualifier(qualifierDepartmentalIdentifier);
         makeKos.setIdentifierAffinityDomain(identifierAffinityDomain);
         makeKos.makeKos();
      } catch (Exception e) {
         e.printStackTrace();
      }
      log.info("EO generateKOS");
   }
	
	/**
	 * Send images (and/or other DICOM objects) to an SCP.
	 * @param scp AEBean for the destination SCP
	 * @param filesFolders zero or more files or folders containing DICOM objects
	 * to send. (If Zero, a DICOM Echo will be sent.
	 */
	public static void sendImages(AEBean scp, String... filesFolders) {
	   try {
	      log.info("BO sendImages");
	      List<String> a = new ArrayList<>();
	      a.add("-c");
	      a.add(scp.getAeTitle() + "@" + scp.getHost() + ":" + 
	         scp.getPort().toString());
	      for (String s : filesFolders) a.add(s);
	      StoreSCU.main(a.toArray(new String[0]));
	   } catch (Exception e) {
	      log.severe(e.getMessage());
	      e.printStackTrace();
	   }
	}

   /**
    * Generates a "new" study from an existing study by changing the patient id
    * and the UIDs.
    * 
    * @param outputFolder complete path to a folder on the file system where the
    * "new" study will be placed. If it does not exist, it will be created. Must
    * have privileges. Everything previously in this directory will be deleted.
    * After the call there will be one file in the directory for each image in
    * the "new" study, with no subdirectories. The image files will be named
    * "999999.dcm", numbered in the order they were processed.
    * 
    * @param inputFolder complete path to a folder on the file system where the
    * study used as a template to build the new study may be found. Must have
    * read privileges. The directory structure under the inputFolder is not
    * relevant, but all files in that structure must be dicom image files which
    * represent the SOPInstances of the study. Nothing in the inputFolder will
    * be modified.
    * 
    * <p><b>Note:</b> Folder names may be given as absolute paths or relative paths, 
    * which will be interpreted in relation to the XDSI root directory.</p>
    * 
    * @param patientIdentifier The patient identifier for the "new" study, in CX
    * format. For example:
    * 
    * <pre>
    *  {@code id-19^^^&1.3.6.1.4.1.21367.2005.3.7&ISO }
    * </pre>
    * 
    * @param studyUID The study Instance UID (0020,000D) for the "new" study.
    * Series and SOPInstance UIDs will be generated by the method.
    * @param accessionNumber The accession number (0008,0050) for the "new"
    * study.
    * @param patientName for example, "Moulton^Fred"
    * @param dateOfBirth for example, 19640523 
    * @param sex for example "M"
    * @param studyDate for example 20141224
    * @throws IOException On any error. TODO Add realistic error handling
    */
	public static void reidentifyStudy(String outputFolder, String inputFolder, 
	   String patientIdentifier, String studyUID, String accessionNumber,
	   String patientName, String dateOfBirth, String sex, String studyDate) 
	   throws IOException {

	   log.info("BO reidentifyStudy");
	   outputFolder = Utility.getXDSIRootPath().resolve(outputFolder).toString();
      inputFolder = Utility.getXDSIRootPath().resolve(inputFolder).toString();
		FileUtils.deleteDirectory(new File(outputFolder));
      Files.createDirectories(Paths.get(outputFolder));
      int fileCounter = 0;

		List<File> srcFiles = (List<File>) FileUtils.listFiles(new File(inputFolder), TrueFileFilter.TRUE,
				TrueFileFilter.TRUE);

		DicomInputStream din = null;
		Map<String, String> seriesIds = new HashMap<String, String>();
		if (patientName.equals("")) {
			patientName = "Moulton^Fred";
		}
		if (dateOfBirth.equals("")) dateOfBirth = "19910101";
		if (sex.equals("")) sex = "M";

		for (File srcFile : srcFiles) {
			try {
				din = new DicomInputStream(srcFile);
				Attributes meta = din.readFileMetaInformation();
				Attributes dataSet = din.readDataset(-1, -1);
				String oldSeriesId = dataSet.getString(Tag.SeriesInstanceUID, "");
				if (seriesIds.containsKey(oldSeriesId) == false) {
					seriesIds.put(oldSeriesId, Identifiers.generateSeriesInstanceUID());
				}
				String newSeriesId = seriesIds.get(oldSeriesId);
				String instanceUID = Identifiers.generateSOPInstanceUID();
				dataSet.setString(Tag.PatientID, VR.LO, patientIdentifier);
				dataSet.setString(Tag.StudyInstanceUID, VR.UI, studyUID);
				dataSet.setString(Tag.SeriesInstanceUID, VR.UI, newSeriesId);
				dataSet.setString(Tag.SOPInstanceUID, VR.UI, instanceUID);
				meta.setString(Tag.MediaStorageSOPInstanceUID, VR.UT, instanceUID);
				dataSet.setString(Tag.AccessionNumber, VR.SH, accessionNumber);
				dataSet.setString(Tag.StudyID, VR.SH, accessionNumber);
				dataSet.setString(Tag.PatientName, VR.PN, patientName);
				dataSet.setString(Tag.Manufacturer, VR.LO, "WUSTL");
				if (! dateOfBirth.equals("")) {
					dataSet.setString(Tag.PatientBirthDate, VR.DA, dateOfBirth);
				}
				if (! sex.equals("")) {
					dataSet.setString(Tag.PatientSex, VR.CS, sex);
				}
				if (! studyDate.equals("")) {
					dataSet.setString(Tag.StudyDate, VR.DA, studyDate);
					dataSet.setString(Tag.SeriesDate, VR.DA, studyDate);
					dataSet.setString(Tag.AcquisitionDate, VR.DA, studyDate);
					dataSet.setString(Tag.ContentDate, VR.DA, studyDate);
					dataSet.setString(Tag.CurveDate, VR.DA, studyDate);
					dataSet.setString(Tag.OverlayDate, VR.DA,  studyDate);
					dataSet.setString(Tag.AcquisitionDateTime, VR.DT, studyDate);
				}

				String newFileName = String.format("%06d", fileCounter++) + ".dcm";
				File out = new File(outputFolder, File.separator + newFileName);
				Files.createFile(out.toPath());
				DicomOutputStream dos = new DicomOutputStream(out);
				dos.writeFileMetaInformation(meta);
				dataSet.writeTo(dos);
				dos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
      log.info("EO reidentifyStudy");
	} // EO reidenifyStudy method
	
	/**
	 * Make {@link org.dcm4che3.data.Attributes Attribute set} for a coded value,
	 * using passed parameters. No validation is done, but values used should
	 * form a valid coded value appropriate to their use.
	 * @param codeValue value of code from code set.
	 * @param codingSchemeDesignator code scheme designator, for example "DCM".
	 * @param codeMeaning human readable code description, from code set
	 * @return Attributes containing Code Value (0008,0100), Coding Scheme
	 * Designator (0008,0102), and CodeMeaning (0008,0104) {@link org.dcm4che3.data.Tag Tags}
	 */
	public static Attributes makeCodedItem(String codeValue, String codingSchemeDesignator, String codeMeaning) {
      Attributes attrs = new Attributes(3);
      attrs.setString(Tag.CodeValue, VR.SH, codeValue);
      attrs.setString(Tag.CodingSchemeDesignator, VR.SH, codingSchemeDesignator);
      attrs.setString(Tag.CodeMeaning, VR.LO, codeMeaning);
      return attrs;
	}
	
	/**
	 * Create a KOSBean by reading data from a DICOM KOS file.
	 * @param path of DICOM KOS file, absolute or relative to 
	 * {@link Utility#getXDSIRootPath()}
	 * @return {link KOSBean} instance.
	 * @throws Exception on error reading DICOM KOS file.
	 */
	public KOSBean readKOS(String path) throws Exception {
	   String aPath = Utility.getXDSIRootPath().resolve(path).toString();
		KOSBean bean = new KOSBean(null);
		
		DicomInputStream din = null;
		din = new DicomInputStream(new File(aPath));
		Attributes meta = din.readFileMetaInformation();
		Attributes dataSet = din.readDataset(-1, -1);
		
		bean.setStudyInstanceUID(dataSet.getString(Tag.StudyInstanceUID));
		bean.setSeriesInstanceUID(dataSet.getString(Tag.SeriesInstanceUID));
		bean.setSopInstanceUID(dataSet.getString(Tag.SOPInstanceUID));
		
		Sequence evidenceSequence = dataSet.getSequence(Tag.CurrentRequestedProcedureEvidenceSequence);
		Iterator<Attributes> it = evidenceSequence.iterator();
		while (it.hasNext()) {
			Attributes attrs = it.next();
			String studyUID = attrs.getString(Tag.StudyInstanceUID);
			KOSStudyBean studyBean = new KOSStudyBean(studyUID, null);
			Sequence seriesSequence = attrs.getSequence(Tag.ReferencedSeriesSequence);
			Iterator<Attributes> seriesIterator = seriesSequence.iterator();
			while (seriesIterator.hasNext()) {
				Attributes seriesAttrs = seriesIterator.next();
				String seriesUID = seriesAttrs.getString(Tag.SeriesInstanceUID);
				String retrieveAETitle = seriesAttrs.getString(Tag.RetrieveAETitle);
				String retrieveLocationUID = seriesAttrs.getString(Tag.RetrieveLocationUID);
				KOSSeriesBean seriesBean = new KOSSeriesBean(seriesUID, retrieveAETitle, retrieveLocationUID, null);
				Sequence instanceSequence = seriesAttrs.getSequence(Tag.ReferencedSOPSequence);
				Iterator<Attributes> itInstance = instanceSequence.iterator();
				while (itInstance.hasNext()) {
					Attributes attrsInstance = itInstance.next();
					String instanceUID = attrsInstance.getString(Tag.ReferencedSOPInstanceUID);
					String classUID    = attrsInstance.getString(Tag.ReferencedSOPClassUID);
					KOSInstanceBean instanceBean = new KOSInstanceBean(instanceUID, classUID);
					seriesBean.addInstanceBean(instanceBean);
				}
				studyBean.addSeriesBean(seriesBean);
			}
			bean.addStudyBean(studyBean);
		}
		//dataSet.getSeq
		
		din.close();
		
		return bean;
	}
	
	public KOSBean manufactureKOSFromUIDs(String path, String retrieveAETitle, String classUID) throws Exception {
		throw new Exception("Method no longer implemented");
	}
	public KOSBean manufactureKOSFromUIDs(String path, String retrieveAETitle, String retrieveLocationUID, String classUID) throws Exception {
      List<String> uidList = Utility.readTextLines(path);
      return manufactureKOSFromUIDs(uidList, retrieveAETitle, retrieveLocationUID, classUID);
	}
	
	public KOSBean manufactureKOSFromUIDs(List<String> uidList, String retrieveAETitle, String retrieveLocationUID, String classUID) throws Exception {
		KOSBean bean = new KOSBean(null);
		
		Iterator<String> it = uidList.iterator();
		HashMap<String, TreeSet<String>> studySeriesMap = new HashMap<String, TreeSet<String>>();
		HashMap<String, TreeSet<String>> seriesInstanceMap = new HashMap<String, TreeSet<String>>();
		
		while (it.hasNext()) {
			String composite = it.next();
			String[] tokens = composite.split(":");
			String studyUID = tokens[0];
			String seriesUID = tokens[1];
			String instanceUID = tokens[2];
			if (studySeriesMap.containsKey(studyUID)) {
				TreeSet<String> x = studySeriesMap.get(studyUID);
				x.add(seriesUID);
			} else {
				TreeSet<String> x = new TreeSet<String>();
				x.add(seriesUID);
				studySeriesMap.put(studyUID, x);
			}
			if (seriesInstanceMap.containsKey(seriesUID)) {
				TreeSet<String> y = seriesInstanceMap.get(seriesUID);
				y.add(instanceUID);
			} else {
				TreeSet<String> y = new TreeSet<String>();
				y.add(instanceUID);
				seriesInstanceMap.put(seriesUID, y);
			}
		}
		Iterator<String> itStudy = studySeriesMap.keySet().iterator();
		while (itStudy.hasNext()) {
			String studyUID = itStudy.next();
			KOSStudyBean studyBean = new KOSStudyBean(studyUID, null);
			TreeSet<String> seriesSet = studySeriesMap.get(studyUID);
			Iterator<String> itSeries = seriesSet.iterator();
			while (itSeries.hasNext()) {
				String seriesUID = itSeries.next();
				KOSSeriesBean seriesBean = new KOSSeriesBean(seriesUID, retrieveAETitle, retrieveLocationUID, null);
				TreeSet<String> instanceSet = seriesInstanceMap.get(seriesUID);
				Iterator<String> itInstance = instanceSet.iterator();
				while (itInstance.hasNext()) {
					String instanceUID = itInstance.next();
					KOSInstanceBean instanceBean = new KOSInstanceBean(instanceUID, classUID);
					seriesBean.addInstanceBean(instanceBean);
				}
				studyBean.addSeriesBean(seriesBean);
			}
			bean.addStudyBean(studyBean);			
		}
		
		return bean;
	}
	
	
	public KOSBean manufactureKOSFromUIDs(KOSBean bean, List<String> uidList, String retrieveAETitle, String retrieveLocationUID, String classUID) throws Exception {
		HashMap<String, TreeSet<String>> studySeriesMap = new HashMap<String, TreeSet<String>>();
		HashMap<String, TreeSet<String>> seriesInstanceMap = new HashMap<String, TreeSet<String>>();
		
		if (bean == null) {
			bean = new KOSBean(null);
		}
		
		Iterator<String> it = uidList.iterator();

		
		while (it.hasNext()) {
			String composite = it.next();
			String[] tokens = composite.split(":");
			String studyUID = tokens[0];
			String seriesUID = tokens[1];
			String instanceUID = tokens[2];
			if (studySeriesMap.containsKey(studyUID)) {
				TreeSet<String> x = studySeriesMap.get(studyUID);
				x.add(seriesUID);
			} else {
				TreeSet<String> x = new TreeSet<String>();
				x.add(seriesUID);
				studySeriesMap.put(studyUID, x);
			}
			if (seriesInstanceMap.containsKey(seriesUID)) {
				TreeSet<String> y = seriesInstanceMap.get(seriesUID);
				y.add(instanceUID);
			} else {
				TreeSet<String> y = new TreeSet<String>();
				y.add(instanceUID);
				seriesInstanceMap.put(seriesUID, y);
			}
		}
		Iterator<String> itStudy = studySeriesMap.keySet().iterator();
		while (itStudy.hasNext()) {
			String studyUID = itStudy.next();
			KOSStudyBean studyBean = new KOSStudyBean(studyUID, null);
			TreeSet<String> seriesSet = studySeriesMap.get(studyUID);
			Iterator<String> itSeries = seriesSet.iterator();
			while (itSeries.hasNext()) {
				String seriesUID = itSeries.next();
				KOSSeriesBean seriesBean = new KOSSeriesBean(seriesUID, retrieveAETitle, retrieveLocationUID, null);
				TreeSet<String> instanceSet = seriesInstanceMap.get(seriesUID);
				Iterator<String> itInstance = instanceSet.iterator();
				while (itInstance.hasNext()) {
					String instanceUID = itInstance.next();
					KOSInstanceBean instanceBean = new KOSInstanceBean(instanceUID, classUID);
					seriesBean.addInstanceBean(instanceBean);
				}
				studyBean.addSeriesBean(seriesBean);
			}
			bean.addStudyBean(studyBean);			
		}
		
		return bean;
	}
	
	public KOSBean manufactureKOSFromCompositeUID(String composite, String retrieveAETitle, String classUID) throws Exception {
		throw new Exception("Method no longer implemented");
	}
		
	
	public KOSBean manufactureKOSFromCompositeUID(String composite, String retrieveAETitle, String retrieveLocationUID, String classUID) throws Exception {
		KOSBean kosBean = new KOSBean(null);
		String[] tokens = composite.split(":");
		String studyUID = "";
		String seriesUID = "";
		String instanceUID = "";
		
		if (tokens.length > 0) studyUID = tokens[0];
		if (tokens.length > 1) seriesUID = tokens[1];
		if (tokens.length > 2) instanceUID = tokens[2];
		KOSStudyBean studyBean = new KOSStudyBean(studyUID, null);
		KOSSeriesBean seriesBean = new KOSSeriesBean(seriesUID, retrieveAETitle, retrieveLocationUID, null);
		KOSInstanceBean instanceBean = new KOSInstanceBean(instanceUID, classUID);
		seriesBean.addInstanceBean(instanceBean);
		studyBean.addSeriesBean(seriesBean);
		kosBean.addStudyBean(studyBean);		
		return kosBean;		
	}
	
	public void KOSToRad69(String pathKOS, String repositoryUniqueId, String pathFullRequest, String pathStudyRequestTransferSyntax) throws Exception {
		KOSBean kosBean = this.readKOS(pathKOS);
		String space1x = "  ";
		String space2x = "    ";
		String space3x = "      ";
		String eol = "\n";
		List<String> studyXferList = new ArrayList<String>();
		if (repositoryUniqueId.equals("REPUID-VAR")) {
			repositoryUniqueId = "$repuid$";
		}
		
		Iterator<KOSStudyBean> iteratorStudy = kosBean.getStudyBeanList().iterator();
//		String studyRequestTransferSyntax = "";
		while (iteratorStudy.hasNext()) {
			KOSStudyBean studyBean = iteratorStudy.next();
			studyXferList.add("<StudyRequest studyInstanceUID=\"" + studyBean.getStudyUID() + "\">");

			Iterator<KOSSeriesBean> iteratorSeries = studyBean.getSeriesBeanList().iterator();
			while (iteratorSeries.hasNext()) {
				KOSSeriesBean seriesBean = iteratorSeries.next();
				studyXferList.add(space1x + "<SeriesRequest seriesInstanceUID=\"" +seriesBean.getSeriesUID() + "\">");
				Iterator<KOSInstanceBean> iteratorInstance = seriesBean.getInstanceBeanList().iterator();
				while (iteratorInstance.hasNext()) {
					KOSInstanceBean instanceBean = iteratorInstance.next();
					studyXferList.add(space2x + "<ihe:DocumentRequest>");
					studyXferList.add(space3x + "<ihe:RepositoryUniqueId>" + repositoryUniqueId + "</ihe:RepositoryUniqueId>");
					studyXferList.add(space3x + "<ihe:DocumentUniqueId>" + instanceBean.getInstanceUID() + "</ihe:DocumentUniqueId>");
					studyXferList.add(space2x + "</ihe:DocumentRequest>");
				}
				studyXferList.add(space1x + "</SeriesRequest>");
			}
			studyXferList.add("</StudyRequest>");
		}
		studyXferList.add("<TransferSyntaxUIDList>");
		studyXferList.add(space1x + "<TransferSyntaxUID>" + "1.2.840.10008.1.2.1" + "</TransferSyntaxUID>");
		studyXferList.add("</TransferSyntaxUIDList>");
		
		PrintWriter writerStudyTransfer = new PrintWriter(pathStudyRequestTransferSyntax, "UTF-8");
		Iterator<String> itString = studyXferList.iterator();
		while (itString.hasNext()) {
			writerStudyTransfer.println(itString.next());
		}
		writerStudyTransfer.close();
		
		PrintWriter writerFullRequest = new PrintWriter(pathFullRequest, "UTF-8");
		writerFullRequest.println("<RetrieveImagingDocumentSetRequest" +
				" xmlns:iherad=\"" + "urn:ihe:rad:xdsi-b:2009\"" +
				" xmlns:ihe=\"" +    "urn:ihe:iti:xds-b:2007\"" +
				">");
		itString = studyXferList.iterator();
		while (itString.hasNext()) {
			writerFullRequest.println(space1x + itString.next());
		}
		writerFullRequest.println("</RetrieveImagingDocumentSetRequest>");
		writerFullRequest.close();		
	}
   /**
    * Test harness for DICOMUtility static methods.
    * <ol>
    * <li>First argument indicates method to test</li>
    * <ol>
    * <li>REIDENTIFY = {@link #reidentifyStudy}</li>
    * <li>MAKEKOS = {@link #generateKOS}</li>
    * <li>SENDIMAGES = {@link #sendImages}</li>
    * </ol>
    * <li>Remainder of arguments are passed to method in order.</li>
    * </ol>
    * 
    * @param args arguments
    */
//   public static void main(String[] args) {
//      String cmd;
//      log = Utility.getLog();
//      cmd = getArg(args, 0);
//      DICOMUtility dicomUtility = new DICOMUtility();
//      try {
//         if (cmd.equalsIgnoreCase("REIDENTIFY")) {
//            DICOMUtility.reidentifyStudy(args[1], args[2], args[3], args[4], args[5], "", "", "", "");
//         } else if (cmd.equalsIgnoreCase("REIDENTIFY_FULL")) {
//            DICOMUtility.reidentifyStudy(args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
//               args[9]);
//         } else if (cmd.equalsIgnoreCase("MAKEKOS")) {
//        	 /*
//        	  * Arguments:
//        	  * + Path to images
//        	  * + Path to KOS folder
//        	  * + Name of the output KOS file (normally kos.dcm)
//        	  * + AE Title in the KOS object (normally WUSTL)
//        	  * + Affinity Domain Patient ID (for metadata.xml file)
//        	  * + Retrieve Location UID (normally 1.3.6.1.4.1.21367.102.1.1)
//        	  */
//        	String fullyQualifiedPatientID = args[5] + "^^^" + Identifiers.getAssigningAuthorityAffinityDomain();
//        	String departmentalIdentifierQualifier = "^^^" + Identifiers.getAssigningAuthorityDepartment();
//            DICOMUtility.generateKOS(args[1], args[2], args[3], args[4], departmentalIdentifierQualifier, fullyQualifiedPatientID, args[6]);
//         } else if (cmd.equalsIgnoreCase("SENDIMAGES")) {
//            AEBean aeBean = AEBean.loadFromConfigurationFile(args[1]);
//            DICOMUtility.sendImages(aeBean, args[2]);
//         } else if (cmd.equalsIgnoreCase("VALIDATEKOS")) {
//            TestRAD68ImagingDocumentSource ids = new TestRAD68ImagingDocumentSource();
//            ids.initializeTest(new String[] { args[1], args[2], args[3], args[4] });
//            ids.runTest();
//            Results results = ids.getResults("RAD-68 IDS test");
//            log.info("Validate KOS Results:" + nl + results.toString());
//         } else if (cmd.equals("KOS-TO-RAD69")) {
//        	 /* Arguments
//        	  * + Path to KOS
//        	  * + Repository Unique ID
//        	  * + Output path: Full request
//        	  * + Output path: Study Request/Transfer Syntax only
//        	  */
//        	 if (args.length != 5) {
//        		 throw new Exception("For KOS-TO-RAD69, arguments are: 1) Path to KOS, 2) Repository Unique ID, 3) Output path: Full request, 4) Output path: Study Request / Transfer Syntax only");
//        	 }
//        	 dicomUtility.KOSToRad69(args[1], args[2], args[3], args[4]);
//         } else {
//        	 log.severe("DICOMUtility: Unrecognized command: " + cmd);
//         }
//        	 
//
//         log.info(cmd + " test completed");
//      } catch (Exception e) {
//         log.severe(cmd + " DICOMUtility:main failed");
//         e.printStackTrace();
//      }
//   }
//
//   private static String getArg(String[] args, int arg) {
//      if (args.length > arg) {
//         String a = args[arg];
//         if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_") || a.equalsIgnoreCase("null")) return null;
//         return a.trim();
//      }
//      return null;
//   }
	
} // EO DICOMUtility class

