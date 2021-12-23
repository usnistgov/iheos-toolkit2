package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XMLParserException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.*;

/**
 * Handles assertions for a test step.
 */
public class AssertionEngine {

   private final static Logger logger = Logger.getLogger(AssertionEngine.class.getName());

   /**
    * All {@code <DataRef>} elements from test step {@code <Assertions>}
    * element, for example:<pre>{@code
    * <DataRef file="THIS" as="output"/>, from
    *
    * <Assertions>
    *    <DataRef file="THIS" as="output"/>
    *    <Assert id="vaidateHome">
    *       count(...) = 1
    *    </Assert>
    * </Assertions>
    * }</pre>
    */
   ArrayList <OMElement> raw_data_refs = null;
   /**
    * All {@code <Assert>} elements from test step  {@code <Assertions>}
    * element.
    */
   ArrayList <OMElement> raw_assertions = null;

   Linkage linkage = null;

   /** log.xml element to put test results under */
   OMElement output = null;

   ToolkitEnvironment toolkitEnvironment;

   public AssertionEngine(ToolkitEnvironment toolkitEnvironment) {
      this.toolkitEnvironment = toolkitEnvironment;
   }

   /**
    * Add detail message to output. passed detail is escaped for HTML, then set
    * as the text of a {@code <Detail>} element, which is added as a child of
    * {@link #output}
    * @param detail string to add.
    */
   public void addDetail(String detail) {
      OMElement d = MetadataSupport.om_factory.createOMElement("Detail", null);
      output.addChild(d);
      d.setText(StringEscapeUtils.escapeHtml(detail));
   }

   /** {@code <Data>} element. All the DataRef elements are children of this */
   OMElement data = null;

   /** this test */
   TestConfig testConfig;

   ArrayList <Assertion> assertions = new ArrayList <Assertion>();
   ArrayList <DataRef> dataRefs = new ArrayList <DataRef>();

   /**
    * set test configuration for this test. Invoked in {@link BasicTransaction}
    * @param testConfig test config
    */
   public void setTestConfig(TestConfig testConfig) {
      this.testConfig = testConfig;
   }

   /**
    * set linkage for this test. Invoked in {@link BasicTransaction}
    * @param linkage for this test
    */
   public void setLinkage(Linkage linkage) {
      this.linkage = linkage;
   }

   BasicTransaction caller = null;
   /**
    * set caller
    * @param s <TestStep>
    */
   public void setCaller(BasicTransaction s) {
      caller = s;
   }

   /**
    * Converts {@code <DataRef>} elements to {@link DataRef} instances list.
    * @throws XdsException on blank file or as attribute in {@code <DataRef>}
    * element.
    */
   void parseDataRefs() throws XdsException {
      for (OMElement xref : raw_data_refs) {
         String file = xref.getAttributeValue(new QName("file"));
         String as = xref.getAttributeValue(new QName("as"));
         if (StringUtils.isBlank(file))
            throw new XdsException("DataRef has missing or empty file attribute", null);
         if (StringUtils.isBlank(as))
            throw new XdsException("DataRef has missing or empty as attribute", null);

         if (file.contains("MGMT")) {
            file = file.replaceFirst("MGMT", testConfig.testmgmt_dir);
         }
         if (file.contains("TEST")) {
            file = file.replaceFirst("TEST", testConfig.testplanDir.getAbsolutePath());
         }

         DataRef dr = new DataRef(file, as);
         dataRefs.add(dr);
      }
   }

   /**
    * Processes {@link DataRef} instances list, parsing each file and adding
    * the results to the {@code <TestStep> <Data>} as a child element.<br/>
    * <b>Special case:</b> file name "THIS" makes a copy of the entire log.xml
    * file to this point and adds it.
    * @throws XMLParserException if file not valid xml.
    * @throws XdsInternalException on an xml parsing error in the "THIS" case -
    * not likely.
    */
   void buildDataModel() throws XMLParserException, XdsInternalException {
      data = MetadataSupport.om_factory.createOMElement(new QName("Data"));
      boolean is_this = false;
      for (DataRef ref : dataRefs) {

         OMElement file_root = null;
         if (ref.file.equals("THIS")) {
            // output holds output of this step. Here we
            // getRetrievedDocumentsModel the output of
            // the entire test plan (entire contents of log.xml)
            // because some test scripts make reference to previous steps
            file_root = (OMElement) ((OMElement) output.getParent()).getParent();
            is_this = true;
         } else {
            try {
               file_root = Parse.parse_xml_file(ref.file);
            } catch (Exception e) {
               // hmmm, input may be in log directory, try that
               file_root = Parse.parse_xml_file(testConfig.logFile.getParent() + File.separator + ref.file);
            }
         }
         OMElement wrapper = MetadataSupport.om_factory.createOMElement(new QName(ref.as));
         data.addChild(wrapper);

         if (is_this) wrapper.addChild(Util.deep_copy(file_root));
         else wrapper.addChild(file_root);
      }
   }

   String date() { // return date in 20081009 format
      StringBuilder sb = new StringBuilder();
      // Send all output to the Appendable model sb
      Formatter formatter = new Formatter(sb, Locale.US);
      Calendar c = new GregorianCalendar();
      formatter.format("%s%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
      formatter.close();
      return sb.toString();
   }
   /**
    * Converts {@code <Assert>} elements to {@link Assertion} instances list. 
    * @throws XdsException on error applying linkage to assertion
    */
   void parseAssertions() throws XdsException {
      for (OMElement asser : raw_assertions) {
         if (linkage != null) {
            linkage.apply(asser);
         }
         assertions.add(new Assertion(toolkitEnvironment, asser, testConfig, date()));
      }
   }

   /**
    * set {@link #raw_data_refs Raw DataRefs list}
    * @param data_refs List of {@code <DataRef>} elements. No default.
    */
   public void setDataRefs(ArrayList <OMElement> data_refs) {
      this.raw_data_refs = data_refs;
   }

   /**
    * set {@link #raw_assertions Raw assertions list}
    * @param assertions List of {@code <Assert>} elements. No default.
    */
   public void setAssertions(ArrayList <OMElement> assertions) {
      this.raw_assertions = assertions;
   }

   /**
    * Set an Element in the log.xml file which will be the parent for output
    * for this AssertionEngine run. Usually called by StepContext, which creates
    * an {@code <Assertions>} element under the {@code <TestStep>} element for
    * this step.
    * @param output parent element for logging output.
    */
   public void setOutput(OMElement output) {
      this.output = output;
   }

   public void run(ErrorReportingInterface err, OMElement assertion_output) throws XdsInternalException {
      ILogger testLogger = new TestLogFactory().getLogger();
      try {
         parseDataRefs();
         buildDataModel();
         parseAssertions();
      } catch (Exception e) {
         err.fail("AssertionEngine Error: " + ExceptionUtil.exception_details(e));
         return;
      }

      testLogger.add_name_value(assertion_output, "CompiledAssertion", assertions.toString());
      testLogger.add_name_value(assertion_output, "RawAssertionData", data);
      testLogger.add_name_value(assertion_output, "AssertionCount", Integer.toString(assertions.size()));

//        logger.info("Compiled Assertions: " + assertions.toString());
//        logger.info("RawAssertionData: " + data);

      try {
         for (Assertion assertion : assertions) {

            // Added to handle assertions based on process attribute value
            if (StringUtils.isNotBlank(assertion.process) || assertion.hasValidations()) {
               caller.processAssertion(this, assertion, assertion_output);
               continue;
            }

            String xpath = assertion.xpath;
            if (xpath.contains("equalsIgnoreCase")) {
               // XPath 1.0 does not offer case insensitive comparison OR translate so a special
               // instruction is needed
               String equalsToken = null;
               String leftSide = null;
               String rightSide = null;
               String[] parts = xpath.split("equalsIgnoreCase");
               if (parts.length == 2) {
                  leftSide = parts[0].toLowerCase();
                  rightSide = parts[1].toLowerCase();
                  equalsToken = "=";
                  assertion.xpath = leftSide + " " + equalsToken + " " + rightSide;
               }
            }

            AXIOMXPath xpathExpression;
            try {
               xpathExpression = new AXIOMXPath(assertion.xpath);
            } catch (Exception e) {
               err.fail("AssertionEngine: exception " + e.getClass().getName() + ": " + e.getMessage() + '\n'
                       + ExceptionUtil.exception_details(e));
               err.fail("Failing expression was " + assertion.id + "(" + assertion.xpath + ")");
               return;
            }
            String result = xpathExpression.stringValueOf(data);


            if (result == null || !result.toLowerCase().equals("true")) {
               OMElement failure = MetadataSupport.om_factory.createOMElement("FailedAssertion", null);
               failure.addAttribute("assertionId", assertion.id, null);
               failure.addAttribute("status", result, null);
               OMElement details = MetadataSupport.om_factory.createOMElement("AssertionText", null);
               failure.addChild(details);
               details.setText(assertion.xpath);
               int equals_index = findNotEqualsNotInBrackets(assertion.xpath);
               if (equals_index == -1) equals_index = findEqualsNotInBrackets(assertion.xpath);
               if (equals_index > 0) {
                  // offer some more details
                  String eqToken = tokenAt(assertion.xpath, equals_index);
                  String left_side_xpath = assertion.xpath.substring(0, equals_index).trim();
                  String right_side_xpath = assertion.xpath.substring(equals_index + eqToken.length()).trim();

                  String left_side_value;
                  if (left_side_xpath.indexOf("//") == -1) left_side_value = left_side_xpath;
                  else
                     left_side_value = (new AXIOMXPath(left_side_xpath)).stringValueOf(data);

                  String right_side_value;
                  if (right_side_xpath.indexOf("//") == -1) right_side_value = right_side_xpath;
                  else
                     right_side_value = (new AXIOMXPath(right_side_xpath)).stringValueOf(data);

                  OMElement leftSide = MetadataSupport.om_factory.createOMElement("LeftSideValue", null);
                  failure.addChild(leftSide);
                  leftSide.setText(left_side_value);

                  OMElement operator = MetadataSupport.om_factory.createOMElement("Operator", null);
                  failure.addChild(operator);
                  operator.setText(tokenAt(assertion.xpath, equals_index));

                  OMElement rightSide = MetadataSupport.om_factory.createOMElement("RightSideValue", null);
                  failure.addChild(rightSide);
                  rightSide.setText(right_side_value);
               }
               testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", assertion.id, "fail");
               err.fail(failure);
            } else {
               testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", assertion.id, "pass");
               err.setInContext("AssertionEngine: assertion " + assertion.id, "pass");
            }
         }

      } catch (Exception e) {
         err.fail("AssertionEngine: exception " + e.getClass().getName() + ": " + e.getMessage() + '\n'
            + ExceptionUtil.exception_details(e));
         return;
      }
   }

   char bracketLeft(String s, int start) {
      for (int i = start; i > 0; i-- ) {
         if (s.charAt(i) == '[') return '[';
         if (s.charAt(i) == ']') return ']';
      }
      return ' ';
   }

   char bracketRight(String s, int start) {
      for (int i = start; i < s.length(); i++ ) {
         if (s.charAt(i) == '[') return '[';
         if (s.charAt(i) == ']') return ']';
      }
      return ' ';
   }

   boolean closeLeft(String s, int start) {
      char c = bracketLeft(s, start);
      return c == ']' || c == ' ';
   }

   boolean openRight(String s, int start) {
      char c = bracketRight(s, start);
      return c == '[' || c == ' ';
   }

   int findEqualsNotInBrackets(String s) {
      int i = s.indexOf('=');
      while (i != -1) {
         if (closeLeft(s, i) && openRight(s, i)) return i;
         i = s.indexOf('=', i + 1);
      }
      return -1;
   }

   int findNotEqualsNotInBrackets(String s) {
      int i = s.indexOf("!=");
      while (i != -1) {
         if (closeLeft(s, i) && openRight(s, i)) return i;
         i = s.indexOf('=', i + 2);
      }
      return -1;
   }

   boolean isWhite(char c) {
      return c == ' ' || c == '\t' || c == '\n';
   }

   String tokenAt(String s, int start) {
      for (int i = start; i < s.length(); i++ )
         if (isWhite(s.charAt(i))) return s.substring(start, i);
      return "";
   }

}
