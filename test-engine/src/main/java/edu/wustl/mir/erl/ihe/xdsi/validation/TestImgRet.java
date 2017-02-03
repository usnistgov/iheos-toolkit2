/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import gov.nist.toolkit.utilities.xml.XmlUtil;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * Validate results of Image Document Set Retrieve transaction.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class TestImgRet extends TestDcmSoap {
   
   private static Logger log = Utility.getLog();
   
   private URL testUrl;
   private OMElement testElmnt;
   
   /**
    * Instantiate test. Example of call:<pre>{@code
      TestImgRet test = new TestImgRet("iig-1001", "RAD-69", test, std)}</pre>
    * @param url String url of test xml file. See 
    * {@link DetailXml} for details of xml file format.
    * @param transId transaction ID of transaction to test. Must match id
    * attribute of a Transaction Element in the .xml file.
    * @param testDir root of test directory tree. See 
    * {@link TestDcmSoap#initializeTest(String[]) TestDcmSoap.initializeTest} 
    * for details.
    * @param stdDir root of std directory tree. See 
    * TestDcmSoap.initializeTest for details.
    * @throws Exception on initialization errors.
    */
   public TestImgRet(String url, String transId, String testDir, String stdDir) 
      throws Exception{
      
      testDir = Utility.getXDSIRootPath().resolve(testDir).resolve(transId).toString();
      stdDir = Utility.getXDSIRootPath().resolve(stdDir).resolve(transId).toString();
      
      initializeTest(new String[] {url, transId, testDir, stdDir});
   }

   @Override
   public void initializeTest(String[] args) throws Exception {
      super.initializeTest(args);
      
      // Load sut-####.xml file
      testUrl = new URL(args[0]);
      testElmnt = XmlUtil.strToOM(IOUtils.toString(testUrl, "UTF-8")); 
      
      // get <Transaction> elements
      List <OMElement> e = XmlUtil.childrenWithLocalName(testElmnt, "Transactions");
      if (e.isEmpty()) throw new Exception("<Transactions> element missing");
      if (e.size() > 1) throw new Exception("Only one <Transactions> element permitted, " + e.size() + " found.");
      e = XmlUtil.childrenWithLocalName(e.get(0), "Transaction");
      
      // Step through transaction elements
      for (OMElement transaction : e) {
         String transactionId = transaction.getAttributeValue(new QName("id"));
         if (transactionId.equals(args[1]) == false) continue;
         String title = transaction.getAttributeValue(new QName("name"));
         if (StringUtils.isBlank(title)) title = transactionId; 
         // Step through component elements
        for (OMElement component : XmlUtil.childrenWithLocalName(transaction, "Component")) {
           String componentId = component.getAttributeValue(new QName("id"));
           StepXml step = new StepXml();
           step.initializeStep(new Object[] {testElmnt, transactionId, componentId,
              args[2], args[3]});
           step.setTitle(title);
           String subTitle = component.getAttributeValue(new QName("subTitle"));
           if (StringUtils.isBlank(subTitle)) subTitle = componentId;
           step.setSubtitle(subTitle);
           steps.add(step);
        }
      } // EO step through transaction elements
   }
   
   /**
    * @param args <ol start=0>
    * <li/>test xml file name
    * <li/>Transaction Id of transaction in that xml file to Test.
    * <li/>results directory path name, absolute or relative to XDSI root. 
    * <li/>std directory path name, absolute or relative to XDSI root.
    * </ol>
    * Note: The directories are "root" result and std directories, for example
    * std/iig/1001. The transaction id will be added to them as an additional
    * directory level. For example transaction id RAD-69,
    * std/iig/1001/RAD-69
    */
   public static void main(String[]args) {
      try {
      TestImgRet test = new TestImgRet(args[0], args[1], args[2], args[3]);
      test.runTest();
      test.reportResults();
      Results results = test.getResults(args[0]);
      log.info("Test Results:" + Utility.nl + results);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
