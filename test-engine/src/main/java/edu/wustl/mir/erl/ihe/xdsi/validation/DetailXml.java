/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;

import edu.wustl.mir.erl.ihe.xdsi.util.XmlUtil;

/**
 * XML Content evaluation based on XML configuration file. File structure:
 * <pre> {@code
     <?xml version="1.0" encoding="UTF-8"?>
     <Test id="testId" name="test name" sut="code">
        <Namespaces>
           <Namespace uri="uri of namespace" prefix="namespace prefix for xpath"/>
              .
        </Namespaces>
        <Transactions>
           <Transaction id="transId" name="transaction name">
              <GetSoap simulatorName="rig_a" simulatorType="rg" 
                       transactionType="xcr.ids" />
              <Component id="compId" subTitle="validation step subtitle">
                 <Assertions>
                    <Assertion name="user readable name"
                               type="assertionType"
                               xpath="xpath of item for assertion"
                               value="value to match
                               success="result category"
                               failure="result category"/>
                     .
                 </Assertions>
              </Component>
                 .
           </Transaction>
              .
        </Transactions>
     </Test>                               
 * }</pre>
 * Notes:<ol>
 * <li/>The name attributes are used in reporting and should be short, but 
 * understandable to the tester.
 * <li/>Test @id should be our id for the test. It defaults to the xml file name.
 * <li/>Test @sut should be the code for the system under test. It defaults to 
 * the portion of the test id before the "-". For example, "iig" or "rig".
 * <li/>Transaction @id must be unique in the xml file, and is passed to the 
 * constructor to indicate which transaction in the xml file is being referenced. 
 * Use the transaction name, for example "RAD-69", adding additional characters
 * if more than one transaction of that type is included in the test. This is
 * the subdirectory under the test number directory for both results and std
 * directories.
 * <li/>Transaction @name is a short, user readable name for the transaction.
 * Default is the Transaction @id.
 * <li/>GetSoap element is ONLY present for those transactions which are 
 * processed by an xdstools2 simulator. It is used to retrieve the SOAP message
 * components from the xdstools2 logs of that simulator for evaluation. It MUST
 * have these attributes:<ul>
 * <li/>@simulatorName, the name of the simulator used, not including its
 * session. For example, for a simulator acme__rig_a, the attribute values 
 * would be "rig_a".
 * <li/>@simulatorType, the xdstools2 simulator type, for example "rg".
 * <li/>@transactionType, the xdstools2 transaction type, for example "xcr.ids".
 * </ul>
 * <li/>Component @id is the name of the .xml file in response directory, one 
 * of:<ul>
 * <li/>RequestHeader
 * <li/>RequestBody
 * <li/>ResponseHeader
 * <li/>ResponseBody</ul> For Assertions of @type "SAME" it is also the name
 * of the .xml file in the stdResponse directory.
 * <li/>Component @subTitle is a short, human readable sub title for the 
 * validation step. Default is the Component @id
 * <li/>Assertion @type is the type of assertion. It must be one of the values
 * of {@link DetailXmlContent.TYPE#name()} although case is ignored.
 * <li/>Assertion @xpath is the XPath expression for the item in the component
 * to be tested.
 * <li/>Assertion @value is the value to be matched against the component value
 * for Assertion @types of "CONSTANT" it is ignored for other Assertion @types.
 * <li/>Assertion @success is the result category for a success on this 
 * assertion. It must be one of the values of {@link CAT#name()} although case
 * is ignored. The default value is "SUCCESS".
 * <li/>Assertion @failure is the result category for a failure on this 
 * assertion. It must be one of the values of {@link CAT#name()} although case
 * is ignored. The default value is "ERROR".
 * <li/> "." indicates preceding element may have multiple instances.
 * </ol>
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class DetailXml extends DetailXmlContent {
   
   /** {@code <Test>} element, which is the root element of the xml file. */
   private Element testElmnt;
   /** {@code <Namespace>} elements. May be empty, but not null */
   private Element[] namespaceElmnts;
   /** {@code <Assertion>} elements for transaction and component ids passed
    * in constructor. Will not be null or empty.
    */
   private Element[] assertionElmnts;
   /** transaction id for transaction to test. passed in constructor */
   private String transactionId;
   /** transaction name for transaction to test, from attribute */
   private String transactionName;
   /** component id for component of transaction to test. passed in constructor. */
   private String componentId;
   
   /**
    * Constructor, loading configuration file.
    * @param url of configuration file.
    * @param transaction in file to load
    * @param component component of transaction in file to load.
    * @throws Exception on error, including IO, xml parsing, invalid xml.
    */
   public DetailXml(Element ele, String transaction, String component) throws Exception {
      String msg = "DetailXMl: transaction id=" + transaction + " component id=" + component;
      log.info("Loading " + msg);
      try {
      testElmnt = ele;
      transactionId = transaction;
      componentId = component;
      
      // load <Namespace> elements
      Element[] e = XmlUtil.getFirstLevelChildElementsByName(testElmnt, "Namespaces");
      if (e.length == 0) throw new Exception("<Namespaces> element missing");
      if (e.length > 1) throw new Exception("Only one <Namespaces> element permitted, " + e.length + " found.");
      namespaceElmnts = XmlUtil.getFirstLevelChildElementsByName(e[0], "Namespace");
      
      // get <Transaction> elements
      e = XmlUtil.getFirstLevelChildElementsByName(testElmnt, "Transactions");
      if (e.length == 0) throw new Exception("<Transactions> element missing");
      if (e.length > 1) throw new Exception("Only one <Transactions> element permitted, " + e.length + " found.");
      e = XmlUtil.getFirstLevelChildElementsByName(e[0], "Transaction");
      
      // look for transactionId we are testing
      boolean componentFound = false;
transactionLoop: for (Element t : e) {
         if (t.getAttribute("id").equalsIgnoreCase(transactionId) == false) continue;
         // found transaction with id
         transactionName = t.getAttribute("name");
         Element[] cs = XmlUtil.getFirstLevelChildElementsByName(t, "Component");
         for (Element c : cs) {
            if (c.getAttribute("id").equalsIgnoreCase(componentId) == false) continue;
            // found component with id
            componentFound = true;
            // look for <Assertions> element
            Element[] as = XmlUtil.getFirstLevelChildElementsByName(c, "Assertions");
            if (as.length == 0) throw new Exception("<Assertions> element missing");
            if (as.length > 1) throw new Exception("Only one <Assertions> element permitted, " + as.length + " found.");
            // load <Assertion> elements
            assertionElmnts = XmlUtil.getFirstLevelChildElementsByName(as[0], "Assertion");
            if (assertionElmnts.length == 0) throw new Exception("No <Assertion> elements found.");
            // validate <Assertion> element attributes
            boolean assertionError = false;
            for (Element a : assertionElmnts) {
               String name = a.getAttribute("name");
               String type = a.getAttribute("type");
               String xpth = a.getAttribute("xpath");
               String value = a.getAttribute("value");
               String success = a.getAttribute("success");
               if (success.isEmpty()) success = "SUCCESS";
               String failure = a.getAttribute("failure");
               if (failure.isEmpty()) failure = "ERROR";
               if (name.isEmpty()) {
                  assertionError = true;
                  name = "[none]";
                  log.warn("Assertion with missing/empty name attribute");
               }
               String m = "Assertion name=" + name + " ";
               TYPE typ = TYPE.forThis(type);
               if (typ == null) {
                  assertionError = true;
                  log.warn(m + " has invalid type value [" + type + "]");
               }
               if (xpth.isEmpty() && typ.needsXpath == true) {
                  assertionError = true;
                  log.warn(m + " has missing/empty xpath attribute");
               }
               if (typ == TYPE.CONSTANT && value.isEmpty()) {
                  assertionError = true;
                  log.warn(m + " has missing/empty value attribute");
               }
               CAT cat = CAT.forThis(success);
               if (cat == null) {
                  assertionError = true;
                  log.warn(m + " has invalid success category value [" + success + "]");
               }
               cat = CAT.forThis(failure);
               if (cat == null) {
                  assertionError = true;
                  log.warn(m + " has invalid failure category value [" + failure + "]");
               }
            } // EO pass Assertion elements
            if (assertionError) throw new Exception("one or more assertion errors found.");
            if (componentFound) break transactionLoop;
         } // EO pass Component elements
         if (!componentFound) throw new Exception("no <Component> matching id found.");
      } // EO pass Transaction elements
      if (transactionName == null) throw new Exception("no <Transaction> matching id found.");
      
      } catch (Exception e) {
         String em = "Error loading " + msg + " - " + e.getMessage();
         log.warn(em);
         throw new Exception(em);
      }
   }

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.DetailXmlContent#initializeTests()
    */
   @Override
   protected void initializeTests() {
      
      desc = transactionName + ":" + componentId;
      
      // Load namespace QNames
      List<QName> qns = new ArrayList<>();
      for (Element ns : namespaceElmnts) {
         qns.add(new QName(ns.getAttribute("uri"), "dummy", ns.getAttribute("prefix")));
      }
      qnames = qns.toArray(new QName[0]);
      
      // Load assertions
      for (Element a : assertionElmnts) {
         String name = a.getAttribute("name");
         TYPE type = TYPE.forThis(a.getAttribute("type"));
         String xpth = a.getAttribute("xpath");
         String value = a.getAttribute("value");
         String success = a.getAttribute("success");
         if (success.isEmpty()) success = "SUCCESS";
         CAT successCat = CAT.forThis(success);
         String failure = a.getAttribute("failure");
         if (failure.isEmpty()) failure = "ERROR";
         CAT failureCat = CAT.forThis(failure);
         this.assertions.add(new XMLAssertion(name, type, xpth, value, successCat, failureCat));
      }
   }

}
