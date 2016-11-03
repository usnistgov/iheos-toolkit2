/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import org.apache.axiom.om.OMElement;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * Step for XML checking from testId.xml file
 */
public class StepXml extends Step {

   /**
    * Initialize step using {@link DetailXml}. Parameters are:<ol start=0>
    * <li/> URL of test.xml file.
    * <li/> String transaction id of test
    * <li/> String component id of component to test
    * <li/> String testDir
    * <li/> String stdDir
    */
   @Override
   public void initializeStep(Object[] args) throws Exception {
      DetailXml d = new DetailXml((OMElement)args[0], (String) args[1], (String) args[2]); 
      String testPfn = (String) args[3] + Utility.fs + "response" + Utility.fs + (String) args[2] + ".xml";
      String stdPfn  = (String) args[4] + Utility.fs + "response" + Utility.fs + (String) args[2] + ".xml";
      d.initializeDetail(testPfn, stdPfn);
      details.add(d);
   }
}
