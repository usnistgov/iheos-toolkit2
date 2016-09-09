/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.util.DICOMUtility;
import edu.wustl.mir.erl.ihe.xdsi.util.KOSBean;
import edu.wustl.mir.erl.ihe.xdsi.util.KOSStudyBean;
import edu.wustl.mir.erl.ihe.xdsi.util.KOSSeriesBean;

/**
 * General Detail Validator using D Clunie's dciodvfy utility
 */
public class DetailDcmKOSCrossWalk extends Detail {
   
   private static Path xdsi;
   
   static {
      xdsi = Paths.get(Utility.getXDSIRoot());
   }
   
   private String testPfn;
   
   /**
    * @param testPfn String pfn of test dicom object file. Absolute path or relative to XDSI Root.
    * @param desc String human readable type of file, for example, "KOS Document".
    */
   @SuppressWarnings("hiding")
   public void initializeDetail(String testPfn,  String desc) {
      this.testPfn = testPfn;
      this.desc = desc;
   }

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Detail#runDetail()
    */
   @Override
   public void runDetail() {
      // Validate file to examine.
      Path dcmPath = xdsi.resolve(testPfn);
      try {
         Utility.isValidPfn(desc + " file ", dcmPath, PfnType.FILE, "r");
      } catch (Exception e) {
         errorCount++;
         errorDetails.add(e.getMessage());
         return;
      }
      DICOMUtility u = new DICOMUtility();
      try {
    	  KOSBean bean = u.readKOS(dcmPath.toString());
    	  runSeriesUID(bean);
      } catch (Exception e) {
    	  errorCount++;
    	  errorDetails.add(e.getMessage());
      }
   }
   
   private void runSeriesUID(KOSBean bean) {
	   String seriesUID = bean.getSeriesInstanceUID();
	   boolean success = true;
	   for (KOSStudyBean studyBean: bean.getStudyBeanList()) {
		   for (KOSSeriesBean seriesBean: studyBean.getSeriesBeanList()) {
			   String referencedSeriesUID = seriesBean.getSeriesUID();
			   if (seriesUID.equals(referencedSeriesUID)) {
				   success = false;
				   errorCount++;
				   errorDetails.add("Series Instance UID of the KOS object was found to match a Series UID in the Referenced Series Sequence: " + seriesUID);
				   errorDetails.add("The Series Instance UID of the KOS object should be distinct from the referenced series objects");
			   }
		   }
	   }
	   if (success) {
		   successCount++;
		   successDetails.add("Series UID compared to all referenced series UIDs and found to be different (as expected)");
	   } 
   }

}
