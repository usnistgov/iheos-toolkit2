/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.RunCommand;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * General Detail Validator using D Clunie's dciodvfy utility
 */
public class DetailDciodvfy extends Detail {
   
   private static String dciodvfyCommand;
   private static Path xdsi;
   
   static {
      dciodvfyCommand = Utility.getDciodvfyCommand();
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
      List<String> cmd = new ArrayList<>();
      cmd.add(dciodvfyCommand);
      cmd.add(dcmPath.toString());
      RunCommand dciodvfy = new RunCommand();
      dciodvfy.runCommand(cmd);
      
      uncategorizedCount++;
      uncategorizedDetails.add("dciodvfy returned status " + dciodvfy.getReturnStatus());
      uncategorizedDetails.addAll(dciodvfy.getOut());
      uncategorizedDetails.addAll(dciodvfy.getErr());
   }

}
