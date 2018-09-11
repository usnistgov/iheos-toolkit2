package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.common.coder.Base64Coder;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Builds a Map {@link #linkage} of key value pairs which can then be applied
 * recursively to xml Elements, modifying them by replacing key instances with
 * the corresponding values in attributes and text. Linkage pairs are collected
 * from:<ul>
 * <li/>$configHome$, set to the SUT home community id if it is defined; for XDS
 * gateway tests.
 *
 */
public class Linkage extends BasicLinkage {
   private final static Logger logger = Logger.getLogger(Linkage.class);
   OMElement instruction_output;
   Metadata m;

   List <OMElement> use_id = null;
   List <OMElement> use_object_ref = null;
   List <OMElement> use_xpath = null;
   List <OMElement> use_repository_unique_id = null;

   /**
    * This is where the various linkages are collected prior to being applied to
    * xml elements. Instance of the key are replaced with the value.
    */
   HashMap <String, String> linkage = new HashMap <String, String>();

   String repUniqueId = null;
   String homeCommunityId = null;
   boolean debug = false;

   public String getRepositoryUniqueId() {
      return repUniqueId;
   }

   /**
    * Add SUT home community id to links, if defined. For cross community
    */
   void addHome() {
      if (testConfig != null && testConfig.configHome != null) {
         addLinkage("$configHome$", testConfig.configHome);
      }
   }

   // m - metadata to modify based on linkage ( if null - no modifications made)
   // instruction_output - log output - place to search previous test steps for
   // linkage targets
   // - if null only previous testplans will be searched
   // use_id - linkage specification (requests) to previous steps
   public Linkage(TestConfig config, OMElement instruction_output, Metadata m, List <OMElement> use_id) {
      super(config);
      this.instruction_output = instruction_output;
      this.m = m;
      this.use_id = use_id;
      this.debug = testConfig.verbose;
      addHome();

      if (debug) logger.info(use_id);
   }

   // use this when need to build use_id list manually
   // For manual usage, call sequence is
   // Linkage l = new Linkage(instruction_output, m);
   // l.add_use_value(symbol1, value1);
   // l.add_use_value(symbol2, value2);
   // l.compile();
   public Linkage(TestConfig config, OMElement instruction_output, Metadata m) {
      super(config);
      this.instruction_output = instruction_output;
      this.m = m;
      this.debug = testConfig.verbose;
      addHome();
   }

   public Linkage(TestConfig config) {
      super(config);
      instruction_output = null;
      m = null;
      if (testConfig != null)
         this.debug = testConfig.verbose;
      addHome();
   }

   public Linkage(TestConfig config, OMElement instruction_output) {
      super(config);
      this.instruction_output = instruction_output;
      m = null;
      this.debug = testConfig.verbose;
      addHome();
   }

   public void setUseObjectRef(List <OMElement> use_object_ref) {
      this.use_object_ref = use_object_ref;
   }

   public void setUseRepositoryUniqueId(List <OMElement> use_repository_unique_id) {
      this.use_repository_unique_id = use_repository_unique_id;
   }

   public void setUseXPath(List <OMElement> use_xpath) {
      this.use_xpath = use_xpath;
   }

   public String get_value(String id) {
      return linkage.get(id);
   }

   public void addLinkage(String symbol, String value) {
      if (linkage == null) linkage = new HashMap <String, String>();
      linkage.put(symbol, value);
   }

   public void addLinkage(Map <String, String> linkageMap) {
      if (linkage == null) linkage = new HashMap <String, String>();
      linkage.putAll(linkageMap);
   }

   public HashMap <String, String> getLinkageMap() {
      return linkage;
   }

   public String get_value(String testdir, String id, String step, String section)
      throws XdsInternalException, XdsInternalException {
      // when used this way, caller is interested in value of id, so use its
      // value as the value for symbol (last parm)
      add_use_id(testdir, id, step, section, id);
      compileUseId();
      return get_value(id);
   }

   // <UseId testdir="../submit1" id="Document01" step="submit_doc"
   // section="AssignedUids" symbol="$uid$"/>
   public void add_use_id(String testdir, String id, String step, String section, String symbol) {
      if (use_id == null) use_id = new ArrayList <OMElement>();
      OMElement use = MetadataSupport.om_factory.createOMElement(new QName("UseId"));
      use.addAttribute("testdir", testdir, null);
      use.addAttribute("id", id, null);
      use.addAttribute("step", step, null);
      use.addAttribute("section", section, null);
      use.addAttribute("symbol", symbol, null);
      use_id.add(use);
   }

   public void add_use_xpath(OMElement use_xpath) {
      if (this.use_xpath == null) this.use_xpath = new ArrayList <OMElement>();
      this.use_xpath.add(use_xpath);
   }

   public void add_use_value(String symbol, String value) {
      if (use_id == null) use_id = new ArrayList <OMElement>();
      OMElement use = MetadataSupport.om_factory.createOMElement(new QName("UseId"));
      use.addAttribute("symbol", symbol, null);
      use.addAttribute("value", value, null);
      use_id.add(use);

   }

   /**
    * Find xml Element for transaction output in log
    * @param wrapper some ancestor element of what we want, for example, a
    * {@code <TestResults>} element.
    * @param target_test_step_id the id attribute value of  the
    * {@code <TestStep>} we are looking for.
    * @param target_transaction_type the local name of the transaction element
    * we are looking for, for example "NullTransaction". If null, any local name
    * ending in "Transaction" will count.
    * @return the requested transaction element, or null
    * @throws XdsInternalException actually, it doesn't ever throw this.
    */
   public OMElement find_instruction_output(OMElement wrapper, String target_test_step_id,
      String target_transaction_type) throws XdsInternalException {
      for (Iterator it = wrapper.getChildElements(); it.hasNext();) {
         OMElement section = (OMElement) it.next();
         if (!section.getLocalName().equals("TestStep")) continue;
         if (!section.getAttributeValue(MetadataSupport.id_qname).equals(target_test_step_id)) continue;
         for (Iterator it1 = section.getChildElements(); it1.hasNext();) {
            OMElement transaction_output = (OMElement) it1.next();
            if (target_transaction_type == null) {
               if (!transaction_output.getLocalName().endsWith("Transaction")) continue;
            } else {
               if (!transaction_output.getLocalName().equals(target_transaction_type)) continue;
            }
            return transaction_output;
         }
      }
      return null;
   }

   public String format_section_and_step(String step_id, String section_name) {
      String section_and_step = "section " + section_name + " step " + step_id;
      return section_and_step;
   }

   /**
    * get parsed log.xml file for desired test step.
    * @param test_dir String path of test step we want log from, relative to
    * this test step. For example, "../12029/submit".
    * @return parsed log.xml file root element.
    * @throws FactoryConfigurationError if your xml parser is really messed up.
    * @throws XdsInternalException if the log.xml file is missing or messed up.
    */
   public OMElement getLogContents(String test_dir) throws FactoryConfigurationError, XdsInternalException {
      if (debug) logger.info("Load LogFile " + getLogFileName(test_dir));
      return Util.parse_xml(new File(getLogFileName(test_dir)));
   }

   /**
    * Get path of log.xml file in another test step.
    * @param test_dir String path of test step we want log from, relative to
    * this test step. For example, "../12029/submit".
    * @return String path to desired log.xml file.
    */
   public String getLogFileName(String test_dir) {
      // return TestConfig.logFile.toString();
      if (testConfig == null) throw new ToolkitRuntimeException("testConfig not initialized");
      if (testConfig.logFile == null) throw new ToolkitRuntimeException("testConfig.logFile not initialized");
      return testConfig.logFile.getParent() + File.separator + test_dir + File.separator + "log.xml";
   }
   /**
    * @return log file directory name for this test step
    */
   public String getLogFileDir() {
      return testConfig.logFile.getParent();
   }
   /**
    * Get log file directory name for another test step.
    * @param test_dir String path of test step we want log from, relative to
    * this test step. For example, "../12029/submit".
    * @return String path to desired log.xml directory.
    */
   public String getLogFileDir(String test_dir) {
      return testConfig.logFile.getParent() + File.separator + test_dir;
   }

   public void replace_string_in_text_and_attributes(OMElement root, String old_text, String new_text)
      throws XdsInternalException {
      private_replace_string_in_text_and_attributes(root, old_text, new_text);
//      logger.info("Result is\n" + new OMFormatter(root).toString());
//      logger.info("");
   }

   private void private_replace_string_in_text_and_attributes(OMElement root, String old_text, String new_text)
            throws XdsInternalException {

      if (root == null) return;
      if (root.getLocalName().equals("Report")) return;
      if (root.getLocalName().equals("UseReport")) return;
      if (root.getLocalName().equals("UseId")) return;

      // don't look inside document contents
      try {
         if (root.getLocalName().equals("Document")
            && root.getNamespace().getNamespaceURI().equals("urn:ihe:iti:xds-b:2007")) return;
      } catch (Exception e) {}

      replaceStringInElement(root, old_text, new_text);

      for (Iterator it = root.getChildElements(); it.hasNext();) {
         OMElement e = (OMElement) it.next();

         private_replace_string_in_text_and_attributes(e, old_text, new_text);
      }

   }


   private void replaceStringInElement(OMElement e, String old_text, String new_text) {
      // text
      String text = e.getText();
//      logger.info("Replace " + old_text + " with " + new_text + " in " + text);
      if (text.contains(old_text)) {
         String oldstuff = escape_pattern(old_text);
         String replacement = Matcher.quoteReplacement(new_text);
         String newtext = text.replaceAll(oldstuff, replacement);
       //  newtext = newtext.trim();
         e.setText("");  // don't know why this is needed but it is on the MAC!!!!!!
         e.setText(newtext);
//         logger.info("    Yielding " + e.getText());
         String finaltext = e.getText();
      }

      // attributes
      for (Iterator ita = e.getAllAttributes(); ita.hasNext();) {
         OMAttribute att = (OMAttribute) ita.next();
         String value = att.getAttributeValue();
         if (value != null && value.contains(old_text)) {
            value = value.replaceAll(escape_pattern(old_text), new_text);
            att.setAttributeValue(value);
         }
      }
   }

   public String escape_pattern(String pattern) {
      // String new_pattern = "\\" + pattern.substring(0, pattern.length()-1) +
      // "\\$";
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < pattern.length(); i++ ) {
         char c = pattern.charAt(i);
         if (c == '$') {
            buf.append("\\");
            buf.append(c);
         } else buf.append(c);
      }
      return buf.toString();
   }

   public HashMap <String, String> compile() throws XdsInternalException, FactoryConfigurationError, MetadataException {
      HashMap <String, String> result = new HashMap <String, String>();
      if (this.use_id != null) result.putAll(compileUseId());
      if (this.use_object_ref != null) result.putAll(compileUseObjectRef());
      if (this.use_xpath != null) result.putAll(compileUseXPath());
      if (this.use_repository_unique_id != null) result.putAll(this.compileUseRepositoryUniqueId());
      return result;
   }

   /**
    * Process {@code <UseRepositoryUniqueId>} elements.  Attributes:<ol>
    * <li/> testdir - the test directory containing the metadata with the
    * desired object reference, relative to the current test directory. If null
    * or blank, the current test directory is used.
    * <li/> step - the {@code <TestStep>} id attribute value for the test step
    * containing the metadata.
    * <li/> symbol - the parameter name to use.
    * @return String repository unique id
    * @throws XdsInternalException on errors, such as expected test, step or
    * other element not found in log.xml
    * @throws MetadataException invalid metadata.
    */
   HashMap <String, String> compileUseRepositoryUniqueId() throws XdsInternalException, MetadataException {
      HashMap <String, String> result = new HashMap <String, String>();

      OMElement metadata_ele = (m == null) ? null : m.getRoot();

      for (OMElement use : use_repository_unique_id) {
         String testdir = use.getAttributeValue(new QName("testdir"));
         String step = use.getAttributeValue(new QName("step"));
         String symbol = use.getAttributeValue(new QName("symbol"));

         if (debug) logger.info("compileUseRepositoryUniqueId:" + "\n  testdir = " +
            testdir + "\n  step    = " + step + "\n  symbol   = " + symbol);

         Metadata m = getResult(testdir, step);
         if (m == null) continue;

         // this call should include the DocumentEntry.id since a SQ
         // could return DEs from multiple repositories. To do this,
         // the id (which would be coded symbolically) would first
         // have to be converted to uuid form.
         String ruid = getRepositoryUniqueId(m);
         if (StringUtils.isBlank(ruid))
            throw new XdsInternalException("repositoryUniqueId from query result metadata is null");
         result.put(symbol, ruid);

         if (metadata_ele != null) replace_string_in_text_and_attributes(metadata_ele, symbol, ruid);
      }
      if (result != null) new TestLogFactory().getLogger().add_name_value(instruction_output, "UseRepositoryUniqueId",
         result.toString());
      return result;
   } // EO compileUseRepositoryUniqueId() method

   /**
    * Get the Metadata from the {@code <Result>} element. Metadata root element
    * must be the first (only) child element of the Result element.
    * @param testdir path to test directory to look in, or blank for this test
    * @param step step id of {@code <TestStep>} to look for
    * @return the Metadata
    * @throws XdsInternalException on various errors, such as test or step not
    * found, log.xml can't be parsed.
    * @throws MetadataException if the designated element is not a valid
    * Metadata root element.
    */
   public Metadata getResult(String testdir, String step) throws XdsInternalException, MetadataException {
      OMElement result_ele = findResultInLog(step, testdir);
      if (result_ele == null) return null;
      OMElement ele = result_ele.getFirstElement();
      Metadata m = MetadataParser.parseNonSubmission(ele);
      if (debug) logger.info("getResult:" + "\ntestdir = " + testdir + "\nstep = " + step + "\nresult =  "
         + result_ele.getLocalName() + "\nele = " + ((ele == null) ? "null" : ele.getLocalName()) + "\nmetadata is "
         + m.getMetadataDescription());
      return m;
   }

   /**
    * Get repository UID from metadata ExtrinsicObject Slot. Does not work if
    * more than one is present.
    *
    * @param m metadata to search
    * @return repository unique id.
    * @throws XdsInternalException on error, such as no repositoryUniqueId Slot
    * found, value is null/blank, or multiple repositoryUniqueId Slots found.
    */
   String getRepositoryUniqueId(Metadata m) throws XdsInternalException {
      repUniqueId = null;
      for (OMElement eo : m.getExtrinsicObjects()) {
         String rui = m.getSlotValue(eo, "repositoryUniqueId", 0);
         if (debug) logger.info("eo = " + eo.getAttributeValue(MetadataSupport.id_qname) + " repositoryUniqueId = " + rui);
         if (StringUtils.isBlank(rui))
            throw new XdsInternalException("RetrieveTransaction: getRepositoryUniqueId(): ExtrinsicObject "
               + eo.getAttributeValue(MetadataSupport.id_qname) + "does not have a repositoryUniqueId attribute");
         if (repUniqueId == null) repUniqueId = rui;
         else if (!rui.equals(repUniqueId)) { throw new XdsInternalException(
            "RetrieveTransaction: getRepositoryUniqueId(): this metadata contains multiple repositorUniqueIds, this tool is not able to deal with this configuration"); }
      }
      return repUniqueId;
   }

   String getHomeCommunityId(Metadata m) {
      homeCommunityId = null;

      for (OMElement ro : m.getAllObjects()) {
         homeCommunityId = m.getHome(ro);
         if (StringUtils.isNotBlank(homeCommunityId)) return homeCommunityId;
      }

      return homeCommunityId;
   }

   String getHomeCommunityId() {
      return homeCommunityId;
   }

   boolean bool(String value) {
      if (value == null) return false;
      return value.equalsIgnoreCase("true");
   }

   /**
    * Process {@code <UseXPath>} elements, if any, adding object references
    * to parameter-value pairs and processing them in metadata. Attributes
    * (also text node):<ul>
    * <li/> <b>testdir</b> - the test directory containing the log.xml file to
    * be parsed, relative to the current test directory. If null or blank, the
    * current test directory is used.
    * <li/> <b>step</b> - the {@code <TestStep>} id attribute value for the test
    * step containing the xml, within the test directory.
    * <li/> <b>file</b> - file containing the xml data to be parsed. This is
    * INSTEAD of the log.xml. testdir and/or step must be blank or file will be
    * ignored.
    * <li/> <b>symbol</b> - parameter name used when adding to linkage.
    * <li/> <b>decodebase64</b> - boolean true if result of xpath is base64
    * encoded and must be decoded. (Any string which matches to "true" case
    * insensitive is true, all other strings and null are false.
    * <li/> <b>text node</b> - the xpath expression used to access the value
    * from the xml.</ul>
    * For example:<pre>{@code
    * <UseXPath testdir="../12342" step="uuid" symbol="$home$">
    *    /TestResults/TestStep[@id='uuid']/.../*[local-name()='ExtrinsicObject'][1]/@home
    * </UseXPath>
    * }</pre>
    * could result in something like: {"$home$", "urn:oid:1.3.6.1.4.1.21367.13.70.101"}
    * being added to the linkage.<p/>
    * @return linkage, may include other parameter-value pairs.
    * @throws XdsInternalException on errors, including can't find directories,
    * files, or xml elements, can't parse xml or xpath expressions...
    */
   HashMap <String, String> compileUseXPath() throws XdsInternalException {
      // symbol => id_value
      // linkage = new HashMap<String, String>();

      OMElement metadata_ele = (m == null) ? null : m.getRoot();
      for (OMElement use : use_xpath) {

         String testdir = use.getAttributeValue(new QName("testdir"));
         String step = use.getAttributeValue(new QName("step"));
         String xpath = use.getText();
         String file = use.getAttributeValue(new QName("file"));
         String symbol = use.getAttributeValue(new QName("symbol"));
         boolean base64decode = bool(use.getAttributeValue(new QName("decodebase64")));

         boolean is_testdir = areNotBlank(testdir, step, xpath, symbol);
         boolean is_mgmt = areNotBlank(file, xpath, symbol);

         if (areFalse(is_testdir, is_mgmt))
            throw new XdsInternalException(": <UseXPath element must contain of these sets of attributes:\n"
               + "testdir, step, symbol, with the body of the element holding the xpath OR\n"
               + "file, symbol, with the body of the element holding the xpath");
         try {
            if (is_testdir) {
               OMElement root = Parse.parse_xml_file(getLogFileName(testdir));
               List <OMElement> test_steps = XmlUtil.decendentsWithLocalName(root, "TestStep");
               OMElement step_ele = null;
               for (OMElement test_step : test_steps) {
                  String step_id = test_step.getAttributeValue(new QName("id"));
                  if (step.equals(step_id)) {
                     step_ele = test_step;
                     break;
                  }
               }
               if (step_ele == null) throw new XdsInternalException(
                  "Linkage:compileUseXPath(): Cannot find TestStep " + step + " in " + getLogFileName(testdir));

               AXIOMXPath xpathExpression = new AXIOMXPath(xpath);
               String result = xpathExpression.stringValueOf(step_ele);

               if (base64decode) result = Base64Coder.decodeString(result);

               addLinkage(symbol, result);
               if (metadata_ele != null) replace_string_in_text_and_attributes(metadata_ele, symbol, result);

            } else {
               if (file.contains("MGMT")) file = file.replaceFirst("MGMT", testConfig.testmgmt_dir);
               OMElement root = Parse.parse_xml_file(file);
               AXIOMXPath xpathExpression = new AXIOMXPath(xpath);
               String result = xpathExpression.stringValueOf(root);

               if (base64decode) result = Base64Coder.decodeString(result);

               addLinkage(symbol, result);
               if (metadata_ele != null) replace_string_in_text_and_attributes(metadata_ele, symbol, result);
            }
         } catch (Exception e) {
            throw new XdsInternalException("Linkage:compileUseXPath(): problem compiling xpath expression\n" + xpath
               + "\nunable to access referenced data:\n" + e.getMessage() + "\n" + ExceptionUtil.exception_details(e));
         }
      }
      if (linkage != null)
         new TestLogFactory().getLogger().add_name_value(instruction_output, "UseXPath", Util.xmlizeHashMap(linkage));

      return linkage;
   }

   /**
    * Process {@code <UseId>} elements, if any, adding object references
    * to parameter-value pairs. Attributes:<ul>
    * <li/> <b>testdir</b> - the test directory containing the metadata with the
    * desired object reference, relative to the current test directory. If null
    * or blank, the current test directory is used.
    * <li/> <b>step</b> - the {@code <TestStep>} id attribute value for the test
    * step containing the metadata, within the test directory. The {@code
    * <*Transaction>} element under this {@code <TestStep>} will be used.
    * <li/> <b>section</b> - element name of child of the transaction element
    * containing the metadata. The method will look for {@code <Assign>}
    * elements under this element.
    * <li/> <b>id</b> the symbol attribute value of the {@code <Assign>} element
    * to be used to set the id. The method will look for an {@code <Assign>}
    * element with this value in its symbol attribute under the section element.
    * <li/> <b>symbol</b> - the parameter name to use when adding this to the
    * linkage parameter-value pair list.
    * <li/> <b>value</b> - the value to substitute for the parameter. If this is
    * present and not blank, the rest of the processing is skipped, and the
    * String values of symbol and value are entered into the linkage. Did not
    * see any of these... perhaps the value attribute is used as a shortcut
    * during test development.</ul>
    * For example, these entries:<pre>{@code
    * <UseId testdir="../119799" id="SubmissionSet01" step="submit_doc"
    *        section="AssignedSourceId" symbol="$SubSet1$"/>
    *
    * <AssignedSourceId>
    *    <Assign symbol="SubmissionSet01" id="1.3.6.1.4.1.21367.4"/>
    * </AssignedSourceId>
    * }</pre>
    * would result in the pair {"$SubSet1$", "1.3.6.1.4.1.21367.4"} being added
    * to the linkage, and updated in the metadata.<p/>
    * @return linkage, which may have other items in it.
    * @throws XdsInternalException on errors, including empty/missing required
    * attributes, expected log elements not found.
    * @throws FactoryConfigurationError if your xml parser is bogus.
    */
   public HashMap <String, String> compileUseId()
      throws XdsInternalException, FactoryConfigurationError {
      // symbol => id_value
      // linkage = new HashMap<String, String>();
      OMElement metadata_ele = (m == null) ? null : m.getRoot();
      for (OMElement use : use_id) {
         if (debug) logger.info("Compiling " + use);

         String id = use.getAttributeValue(new QName("id"));
         String step_id = use.getAttributeValue(new QName("step"));
         String section_name = use.getAttributeValue(new QName("section"));
         String symbol = use.getAttributeValue(new QName("symbol"));
         String value = use.getAttributeValue(new QName("value"));
         String test_dir = use.getAttributeValue(new QName("testdir"));

         boolean by_value = false;
         if (StringUtils.isNotBlank(symbol) && StringUtils.isNotBlank(value)) by_value = true; // ok
                                                                                               // combination
         else
            if (id == null || id.equals("") || step_id == null || step_id.equals("") || section_name == null
               || section_name.equals("") || symbol == null
               || symbol.equals("")) {
               throw new XdsInternalException(": <UseId element must have id, type, and symbol attributes"
                 + "\n OR  symbol and value must be set programatically " + "\nid = " + id + "\nstep = " + step_id
                 + "\nsection = " + section_name + "\nsymbol = " + symbol + "\nvalue = " + value + "\ntestdir = "
                 + test_dir); }

         if (debug) logger.info("by value is " + by_value);

         OMElement transaction_output;
         if (by_value) {
            if (debug) logger.info("addLinkage by value symbol=" + symbol + "  value=" + value);
            addLinkage(symbol, value);
            if (metadata_ele == null) throw new XdsInternalException("metadata_ele is null");
            replace_string_in_text_and_attributes(metadata_ele, symbol, value);
         } else {
            if (StringUtils.isNotBlank(test_dir)) {
               // look in previous log file
               OMElement log = getLogContents(test_dir);
               transaction_output = find_instruction_output(log, step_id, null);
               if (transaction_output == null) { throw new XdsInternalException("Linkage:CompileUseId(): "
                  + format_section_and_step(step_id, section_name) + " Transaction with step_id " + step_id
                  + " cannot be found in log file " + getLogFileName(test_dir)); }
            } else {
               // look in this log file
               transaction_output = null;
               if (instruction_output != null)
                  transaction_output = find_previous_instruction_output(instruction_output, step_id, null);
               if (transaction_output == null) { throw new XdsInternalException(
                  "Linkage compiler: cannot find transaction output in this log file for "
                     + format_section_and_step(step_id, section_name)); }
            }

            OMElement section = XmlUtil.firstChildWithLocalName(transaction_output, section_name);
            if (section == null) throw new XdsInternalException(
               format_section_and_step(step_id, section_name) + " not found in any previous step");

            if (debug) logger.info("section is " + section);

            boolean foundit = false; // has <Assign> matching id been found?
            for (OMElement assign : XmlUtil.childrenWithLocalName(section, "Assign")) {
               String symbol_value = assign.getAttributeValue(new QName("symbol"));
               String id_value = assign.getAttributeValue(new QName("id"));
               if (debug) logger.info("Assign symbol=" + symbol_value + "  value=" + id_value + " looking for id=" + id);

               if (StringUtils.isBlank(symbol_value)) throw new XdsInternalException(
                  format_section_and_step(step_id, section_name) + " empty assign section (no symbol attribute)");
               if (StringUtils.isBlank(id_value)) throw new XdsInternalException(
                  format_section_and_step(step_id, section_name) + " empty assign section (no id attribute)");

               if (!symbol_value.equals(id)) continue;
               if (section.equals("AssignedPatientId")) {
                  new TestMgmt(testConfig).assignPatientId(m, id_value);
               } else {
                  if (debug) logger.info("addLinkage symbol=" + symbol + "  value=" + value);
                  addLinkage(symbol, id_value);
                  foundit = true;
                  if (metadata_ele != null) replace_string_in_text_and_attributes(metadata_ele, symbol, id_value);
               }
            }
            if (!foundit) throw new XdsInternalException("Linkage Compiler: cannot find definition of id " + id
               + " from " + test_dir + " step " + step_id + " section " + section_name);
         }
      }
      if (linkage != null)
         new TestLogFactory().getLogger().add_name_value(instruction_output, "UseId", Util.xmlizeHashMap(linkage));
      return linkage;
   }

   /**
    * apply linkage to metadata root element. See {@link #apply(OMElement)}
    * @throws XdsInternalException on error
    */
   /**
    * @throws XdsInternalException
    */
   public void compileLinkage() throws XdsInternalException {

      OMElement root = (m == null) ? null : m.getRoot();

      apply(root);
   }

   /**
    * same as {@link #apply(OMElement)}
    * @param root apply to this.
    * @throws XdsInternalException on error.
    */
   public void compileLinkage(OMElement root) throws XdsInternalException {
      apply(root);
   }

   /**
    * Apply substitutions to passed element.<br/>
    * The substitutions are a set of parameter-value pairs which have been
    * collected in this Linkage instance. Each instance of parameter is
    * replaced by the corresponding value.<br/>
    * The passed root element and its descendants are processed. Attribute
    * values and text nodes are processed looking for parameter matches.<br/>
    * Report, UseReport, UseId, and xds-b Document elements are not processed.    *
    * @param root element to process.
    * @throws XdsInternalException on error, but not really.
    */
   public void apply(OMElement root) throws XdsInternalException {
      for (String key : linkage.keySet()) {
         String value = linkage.get(key);
         // logger.info(String.format("apply %s:%s to %s", key, value,new
         // OMFormatter(root).toString()));
         replace_string_in_text_and_attributes(root, key, value);
      }
   }

   /**
    * Process {@code <UseObjectRef>} elements, if any, adding object references
    * to parameter-value pairs. Attributes:<ol>
    * <li/> testdir - the test directory containing the metadata with the
    * desired object reference, relative to the current test directory. If null
    * or blank, the current test directory is used.
    * <li/> step - the {@code <TestStep>} id attribute value for the test step
    * containing the metadata.
    * <li/> symbol - the parameter name to use.
    * <li/> index - the object reference from the metadata to use. Must parse to
    * an integer in the range of object references in the metadata (0 based).</ol>
    * All {@code <UseObjectRef>} elements in the transaction element are
    * processed, and the resulting parameter-value pairs are added to the
    * linkage.
    *
    * <p/>Example:<pre>{@code
    * <UseObjectRef testdir="../12309" index="0" symbol="$doc_uuid_1$" step="finddocs"/>
    * }</pre>
    * @return the linkage. Note that this may contain other parameter-value
    * pairs not generated from {@code <UseObjectRef>} elements.
    * @throws XdsInternalException if {@code <UseObjectRef>} attributes are
    * missing or malformed, if the index is out of range for the metadata, or
    * something wasn't found were it was supposed to be.
    * @throws FactoryConfigurationError if your parser setup is bogus.
    * @throws MetadataException if the referenced metadata is bogus.
    */
   public HashMap <String, String> compileUseObjectRef()
      throws XdsInternalException, FactoryConfigurationError, MetadataException {

      OMElement metadata_ele = (m == null) ? null : m.getRoot();

      for (OMElement use : use_object_ref) {
         String step_id = use.getAttributeValue(new QName("step"));
         String index = use.getAttributeValue(new QName("index"));
         String symbol = use.getAttributeValue(new QName("symbol"));
         String test_dir = use.getAttributeValue(new QName("testdir"));

         if (StringUtils.isBlank(step_id) ||
             StringUtils.isBlank(index) || StringUtils.isBlank(symbol))
            throw new XdsInternalException("<UseObjectRef element must have testdir, step, index, and symbol attributes"
               + "\nindex = " + index + "\nstep = " + step_id + "\nsymbol = " + symbol + "\ntestdir = " + test_dir);

         int index_i = Integer.parseInt(index);
         if (index_i < 0) throw new XdsInternalException(
            "<UseObjectRef element has index less than zero [" + index + "] - invalid index");

         OMElement transaction_output = find_transaction_in_log(step_id, test_dir);

         OMElement result = XmlUtil.firstChildWithLocalName(transaction_output, "Result");
         if (result == null) throw new XdsInternalException(
            "Cannot find Result section in log of step " + step_id + " in test directory " + test_dir);

         Metadata m = MetadataParser.parseNonSubmission(result.getFirstElement());

         List <OMElement> object_refs = m.getObjectRefs();
         if (index_i >= object_refs.size()) throw new XdsInternalException("<UseObjectRef requests index of " + index_i
            + " but query retured only [" + object_refs.size() + "] ObjectRefs");

         String value = object_refs.get(index_i).getAttributeValue(MetadataSupport.id_qname);
         addLinkage(symbol, value);
         if (metadata_ele != null) replace_string_in_text_and_attributes(metadata_ele, symbol, value);
      }
      if (linkage != null) new TestLogFactory().getLogger().add_name_value(instruction_output, "UseObjectRef",
         Util.xmlizeHashMap(linkage));
      return linkage;
   }

   /**
    * Returns the first transaction element in a test step
    * @param step_id desired {@code <TestStep>} element id attribute value
    * @param test_dir test step directory path, relative to this test step. For
    * example, "../12029/submit"
    * @return first child element of this test step with name "*Transaction"
    * @throws FactoryConfigurationError if you're really messed up.
    * @throws XdsInternalException if the log file you're looking for isn't
    * there, or can't be parsed, or doesn't have the test step you are looking
    * for, or that step doesn't have any transactions in it.
    */
   private OMElement find_transaction_in_log(String step_id, String test_dir)
      throws FactoryConfigurationError, XdsInternalException {
      OMElement transaction_output;
      if (StringUtils.isNotBlank(test_dir)) {
         // look in previous log file
         OMElement log = getLogContents(test_dir);
         transaction_output = find_instruction_output(log, step_id, null);
         if (transaction_output == null) { throw new XdsInternalException(format_section_and_step(step_id, "any")
            + " Transaction with step_id " + step_id + " cannot be found in log file " + getLogFileName(test_dir)); }
      } else {
         // look in this log file
         transaction_output = null;
         if (instruction_output != null)
            transaction_output = find_previous_instruction_output(instruction_output, step_id, null);
         if (transaction_output == null) { throw new XdsInternalException(
            format_section_and_step(step_id, "any") + " Transaction not found in any previous step"); }
      }
      return transaction_output;
   }

   /**
    * Get the {@code <Result>} element for a specified test and step.
    * @param step_id desired {@code <TestStep>} element id attribute value
    * @param test_dir test step directory path, relative to this test step. For
    * example, "../12029/submit"
    * @return the {@code <Result>} element for the transaction in this test and
    * step.
    * @throws FactoryConfigurationError if you're really messed up.
    * @throws XdsInternalException if directory or log.xml not found, or can't
    * be parsed, or test step or result elements can't be found.
    */
   public OMElement findResultInLog(String step_id, String test_dir)
      throws FactoryConfigurationError, XdsInternalException {

      OMElement transaction_output = find_transaction_in_log(step_id, test_dir);

      if (transaction_output == null)
         throw new XdsInternalException("Linkage:findResultInLog(): Cannot find *Transaction in log of step " + step_id
            + " in " + test_dir + "/log.xml");

      OMElement result = XmlUtil.firstChildWithLocalName(transaction_output, "Result");
      if (result == null) throw new XdsInternalException(
         "Linkage:findResultInLog(): Cannot find Result in log of step " + step_id + " in " + test_dir + "/log.xml");

      if (debug) logger.info("findResultInLog\n" + result.toString());

      return result;
   }

   /**
    * Gets the transaction element of the previous (or a previous) test step.
    * @param this_instruction_output current transaction element (one level
    * below {@code <TestStep>} element. For example, a {@code
    * <RetrieveTransaction>} element.
    * @param target_test_step_id {@code <TestStep>} element id attribute value
    * of the step we are looking for, for example, "register".
    * @param target_transaction_type local name of instruction element we are
    * looking for, for example "RegisterTransaction". If null, any name ending
    * with "Transaction" will be used.
    * @return the instruction element, or null if not found.
    * @throws XdsInternalException on error, but not really.
    */
   protected OMElement find_previous_instruction_output(OMElement this_instruction_output, String target_test_step_id,
      String target_transaction_type) throws XdsInternalException {
      // example target_transaction_type is "RegisterTransaction"
      // if (debug) System.out.println("Searching for step id=" + target_test_step_id + "
      // and transaction type " + target_transaction_type);
      OMElement this_step_output = (OMElement) this_instruction_output.getParent();
      OMElement step_output = null;
      step_output = (OMElement) this_step_output.getPreviousOMSibling();
      while (step_output != null) {
         String step_output_id = step_output.getAttributeValue(new QName("id"));
         if (step_output_id != null && step_output_id.equals(target_test_step_id)) {
            OMElement transaction_output =
               (target_transaction_type == null) ? XmlUtil.firstChildWithLocalNameEndingWith(step_output, "Transaction")
                  : XmlUtil.firstChildWithLocalName(step_output, target_transaction_type);
            return transaction_output;
         }
         step_output = (OMElement) step_output.getPreviousOMSibling();
      }
      return null;
   }

   /**
    * Are all the passed Strings not blank, that is, not null, empty strings, or
    * just whitespace.
    * @param strings to be checked
    * @return true if all strings are non blank, false otherwise.
    */
   private boolean areNotBlank(String... strings ) {
      for (String str : strings) {
         if (StringUtils.isBlank(str)) return false;
      }
      return true;
   }

   /**
    * Are all the passed boolean values false?
    * @param bs passed booleans
    * @return true if all are false, false otherwise.
    */
   private boolean areFalse(boolean... bs ) {
      for (boolean b : bs) {
         if (b) return false;
      }
      return true;
   }

}
