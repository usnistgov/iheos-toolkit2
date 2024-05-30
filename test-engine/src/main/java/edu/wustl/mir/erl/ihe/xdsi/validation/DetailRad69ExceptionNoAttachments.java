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

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.RunCommand;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * Detail validator for RAD69 Transactions. This detail validation tests for the case of no attachments expected.
 */
public class DetailRad69ExceptionNoAttachments extends Detail {
   
   private String testPfn;
   
   /**
    * @param testPfn String pfn of folder with RAD69 response data.
    * @param desc String human readable label; most likely a test number (101, 102, ...).
    */
   @SuppressWarnings("hiding")
   public void initializeDetail(String testPfn,  String desc) {
      this.testPfn = testPfn;
      this.desc = desc;
   }

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Detail#runDetail()
    */
   /* (non-Javadoc)
 * @see edu.wustl.mir.erl.ihe.xdsi.validation.Detail#runDetail()
 */
@Override
   public void runDetail() {
	Path p = Paths.get(testPfn).resolve("attachments");
	if (! Files.exists(p)) {
		errorCount++;
		errorDetails.add("Folder does not exist; this is part of validation input: " + p.toString());
	} else {
		File f = new File(p.toString());
		String[] folderContents = f.list();
		if (folderContents.length == 0) {
			successCount++;
			successDetails.add("As expected, found no attachments in folder: " + p.toString());
		} else {
			errorCount++;
			errorDetails.add("Found (" + folderContents.length + ") attachments in folder: " + p.toString());
			errorDetails.add(" In this exception test, expected to find no attachments");
		}
	}
   }

}
