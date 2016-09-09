/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.validation.DCMAssertion.TYPE;

/**
 * Base class for detail comparing two sets of dicom documents.
 */
public class DetailDcmSetContent extends DetailDcmSequenceContent  {
   
   private static Logger log = null;

   @Override
   protected void initializeTest() {
      assertions = new ArrayList<>();
      // SOP Class UID (0008,0016)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPClassUID));
      // SOP Instance UID (0008,0018)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SOPInstanceUID));
      // Patient ID (0010,0020)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientID));
      // Patient's Birth Date (0010,0030)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientBirthDate));
      // Patient's Sex (0010,0040)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.PatientSex));
      // Study Instance UID (0020,000D)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.StudyInstanceUID));
      // Series Instance UID (0020,000E)
      assertions.add(new DCMAssertion(TYPE.SAME, Tag.SeriesInstanceUID));
   }

   /**
    * args[0] String directory containing test images.
    * args[1] String directory containing std images.
    * Directories can be absolute, or relative to XDSI root.
    */
   @Override
   protected void initializeDetail(Object[] args) throws Exception {
      uniqueSequenceTag = Tag.SOPInstanceUID;
      desc = "dicom files";
      if (assertions == null)
    	  initializeTest();
 //        throw new Exception("Must run initializeTests before initializeDetail");
      List<Integer> a = new ArrayList<>();
      boolean flag = false;
      for (DCMAssertion assertion : assertions) {
         a.add(assertion.tag);
         if (assertion.tag == uniqueSequenceTag) flag = true;
      }
      if (flag == false) a.add(uniqueSequenceTag);
      int[] tags = new int[a.size()];
      for (int i = 0; i < a.size(); i++) tags[i] = a.get(i);      
      
      Path root = Paths.get(Utility.getXDSIRoot());
      Path testPath = root.resolve((String) args[0]);
      Utility.isValidPfn("test dicom images" , testPath, PfnType.DIRECTORY, "r");
      test = loadAttributesList(testPath, tags);
      
      Path stdPath = root.resolve((String) args[1]);
      Utility.isValidPfn("std dicom images" , stdPath, PfnType.DIRECTORY, "r");
      std = loadAttributesList(stdPath, tags);      
   }

   private List<Attributes> loadAttributesList(Path path, int[]tags) throws Exception {
      List<Attributes> list = new ArrayList<>();
      DicomInputStream din = null;
      try {
         List<File> files = (List<File>) FileUtils.listFiles(path.toFile(), 
            TrueFileFilter.TRUE, TrueFileFilter.TRUE);
         for (File file : files) {
            din = new DicomInputStream(file);
            Attributes dataSet = din.readDataset(-1, -1);
            list.add(new Attributes(dataSet, tags));
            din.close();
         }
         return list;
      } finally {
         if (din != null) din.close();
      }
   }
   /**
    * Test harness.
    * <ol>
    * <li>First argument indicates method to test</li>
    * <ol>
    * <li>RUNTEST = initialize, run, get results</li>
    * </ol>
    * <li/>Remainder of arguments are passed to method in order.
    * <li/>args[1] String directory containing test dicom objects.
    * <li/>args[2] String directory containing std dicom objects.
    * </ol>
    * Note: Directories may be entered as absolute, or relative to XDSI root.
    * 
    * @param args arguments
    */
   public static void main(String[] args) {
      String cmd;
      log = Utility.getLog();
/*      if (args.length > 0) {
         cmd = args[0];
         log.info("Running " + cmd + " test");
         try {
            if (cmd.equalsIgnoreCase("RUNTEST")) {
               DetailDcmSetContent test = new DetailDcmSetContent();
               test.initializeTest();
               test.initializeDetail(new Object[] {args[1], args[2]});
               test.runDetail();
               Results results = new Results();
               test.getResults(results);
               log.info("Test Results:" + Utility.nl + results);
            }

            log.info(cmd + " test completed");
         } catch (Exception e) {
            log.fatal(cmd + " test failed");
            e.printStackTrace();
         }
      }*/
   }
   

}
