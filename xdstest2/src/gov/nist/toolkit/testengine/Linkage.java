package gov.nist.toolkit.testengine;

import gov.nist.toolkit.common.coder.Base64Coder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

public class Linkage extends BasicLinkage {
	OMElement instruction_output;
	Metadata m;

	List<OMElement> use_id = null;
	List<OMElement> use_object_ref = null;
	List<OMElement> use_xpath = null;
	List<OMElement> use_repository_unique_id = null;

	HashMap<String, String> linkage = new HashMap<String, String>();   // symbol => id_value
	String repUniqueId = null;
	String homeCommunityId = null;
	boolean debug = false;

	public String getRepositoryUniqueId() { return repUniqueId; }

	void addHome() {
		if (testConfig.configHome != null) {
			addLinkage("$configHome$", testConfig.configHome);
		}
	}

	// m - metadata to modify based on linkage  ( if null - no modifications made)
	// instruction_output - log output - place to search previous test steps for linkage targets 
	//     - if null only previous testplans will be searched 
	// use_id - linkage specification (requests) to previous steps
	public Linkage(TestConfig config, OMElement instruction_output, Metadata m, List<OMElement> use_id) {
		super(config);
		this.instruction_output = instruction_output;
		this.m = m;
		this.use_id = use_id;
		this.debug = testConfig.verbose;
		addHome();

		if (debug) 
			System.out.println(use_id);
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
		this.m = m ;
		this.debug = testConfig.verbose;
		addHome();
	}

	public Linkage(TestConfig config) {
		super(config);
		instruction_output = null;
		m = null;
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

	public void setUseObjectRef(List<OMElement> use_object_ref) {
		this.use_object_ref = use_object_ref;
	}

	public void setUseRepositoryUniqueId(List<OMElement> use_repository_unique_id) {
		this.use_repository_unique_id = use_repository_unique_id;
	}

	public void setUseXPath(List<OMElement> use_xpath) {
		this.use_xpath = use_xpath;
	}

	public String get_value(String id) {
		return linkage.get(id);
	}

	public void addLinkage(String symbol, String value) {
		if (linkage == null)
			linkage = new HashMap<String, String>();
		linkage.put(symbol, value);
	}
	
	public void addLinkage(Map<String, String> linkageMap) {
		if (linkage == null)
			linkage = new HashMap<String, String>();
		linkage.putAll(linkageMap);
	}

	public HashMap<String, String> getLinkageMap() {
		return linkage;
	}

	public String get_value(String testdir, String id, String step, String section) throws XdsInternalException, XdsInternalException {
		// when used this way, caller is interested in value of id, so use its value as the value for symbol (last parm)
		add_use_id(testdir, id, step, section, id);
		compileUseId();
		return get_value(id);
	}

	// <UseId testdir="../submit1" id="Document01" step="submit_doc"
	//     section="AssignedUids" symbol="$uid$"/>
	public void add_use_id(String testdir, String id, String step, String section, String symbol) {
		if (use_id == null)
			use_id = new ArrayList<OMElement>();
		OMElement use = MetadataSupport.om_factory.createOMElement(new QName("UseId"));
		use.addAttribute("testdir", testdir, null);
		use.addAttribute("id", id, null);
		use.addAttribute("step", step, null);
		use.addAttribute("section", section, null);
		use.addAttribute("symbol", symbol, null);
		use_id.add(use);
	}

	public void add_use_xpath(OMElement use_xpath) {
		if (this.use_xpath == null)
			this.use_xpath = new ArrayList<OMElement>();
		this.use_xpath.add(use_xpath);
	}

	public void add_use_value(String symbol, String value) {
		if (use_id == null)
			use_id = new ArrayList<OMElement>();
		OMElement use = MetadataSupport.om_factory.createOMElement(new QName("UseId"));
		use.addAttribute("symbol", symbol, null);
		use.addAttribute("value", value, null);
		use_id.add(use);

	}

	public OMElement find_instruction_output(OMElement wrapper, String target_test_step_id, 
			String target_transaction_type) throws XdsInternalException {
		for (Iterator it=wrapper.getChildElements(); it.hasNext(); ) {
			OMElement section = (OMElement) it.next();
			if ( !section.getLocalName().equals("TestStep"))
				continue;
			if ( ! section.getAttributeValue(MetadataSupport.id_qname).equals(target_test_step_id))
				continue;
			for (Iterator it1=section.getChildElements(); it1.hasNext(); ) {
				OMElement transaction_output = (OMElement) it1.next();
				if ( target_transaction_type == null) {
					if ( !transaction_output.getLocalName().endsWith("Transaction"))
						continue;
				} else {
					if ( !transaction_output.getLocalName().equals(target_transaction_type))
						continue;
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

	public OMElement getLogContents(String test_dir)
	throws FactoryConfigurationError, XdsInternalException {
		if (debug) System.out.println("Load LogFile " + getLogFileName(test_dir));
		return Util.parse_xml(new File(getLogFileName(test_dir)));
	}

	public String getLogFileName(String test_dir) {
		//return TestConfig.logFile.toString();
		return testConfig.logFile.getParent() + File.separator + test_dir + File.separator + "log.xml";
	}

	public void replace_string_in_text_and_attributes(OMElement root, String old_text, String new_text) throws XdsInternalException {

		if (root == null)
			return;
		
		// don't look inside document contents
		try {
			if (root.getLocalName().equals("Document") &&
					root.getNamespace().getNamespaceURI().equals("urn:ihe:iti:xds-b:2007"))
				return;
		} catch (Exception e) {}

		replaceStringInElement(root, old_text, new_text);

		for (Iterator it=root.getChildElements(); it.hasNext(); ) {
			OMElement e = (OMElement) it.next();

			try {
				replaceStringInElement(e, old_text, new_text);
			} catch (Exception ex) {
				throw new XdsInternalException("Error trying to replace [" + old_text + "] with [" +
				new_text + "] in element " + e.getLocalName(), ex		);
				
			}

			// recurse
			replace_string_in_text_and_attributes(e, old_text, new_text);
		}

	}

	private void replaceStringInElement(OMElement e, String old_text,
			String new_text) {
		// text
		String text = e.getText();
		if (text.indexOf(old_text) != -1) {
			text = text.replaceAll(escape_pattern(old_text), new_text);
			e.setText(text);
		}

		// attributes
		for (Iterator ita=e.getAllAttributes(); ita.hasNext(); ) {
			OMAttribute att = (OMAttribute) ita.next();
			String value = att.getAttributeValue();
			if (value.indexOf(old_text) != -1) {
				value = value.replaceAll(escape_pattern(old_text), new_text);
				att.setAttributeValue(value);
			}
		}
	}

	String escape_pattern(String pattern) {  
		//	String new_pattern = "\\" + pattern.substring(0, pattern.length()-1) + "\\$";
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (c == '$') {
				buf.append("\\");
				buf.append(c);
			} else buf.append(c);
		}
		return buf.toString();
	}

	public HashMap<String, String> compile() throws XdsInternalException,
	FactoryConfigurationError, MetadataException {
		HashMap<String, String> result = new HashMap<String, String>();
		if ( this.use_id != null)
			result.putAll(compileUseId());
		if (this.use_object_ref != null)
			result.putAll(compileUseObjectRef());
		if (this.use_xpath != null)
			result.putAll(compileUseXPath());
		if (this.use_repository_unique_id != null)
			result.putAll(this.compileUseRepostoryUniqueId());
		return result;
	}

	HashMap<String, String> compileUseRepostoryUniqueId() throws XdsInternalException, MetadataException {
		HashMap<String, String> result = new HashMap<String, String>();

		OMElement metadata_ele = (m == null) ? null : m.getRoot();

		for (OMElement use : use_repository_unique_id) {
			String testdir = use.getAttributeValue(new QName("testdir"));
			String step = use.getAttributeValue(new QName("step"));
			String symbol = use.getAttributeValue(new QName("symbol"));

			if (debug) 
				System.out.println("compileUseRepositoryUniqueId:" +
						"\ntestdir = " + testdir +
						"\nstep = " + step +
						"\nsymbol = " + symbol
				);

			Metadata m = getResult(testdir, step);
			if (m == null)
				return result;

			// this call should include the DocumentEntry.id since a SQ
			// could return DEs from multiple repositories.  To do this,
			// the id (which would be coded symbolically) would first
			// have to be converted to uuid form.
			String ruid = getRepositoryUniqueId(m);
			if (ruid == null || ruid.equals(""))
				throw new XdsInternalException("repositoryUniqueId from query result metadata is null");
			result.put(symbol, ruid);

			if (metadata_ele != null)
				replace_string_in_text_and_attributes(metadata_ele, symbol, ruid);
		}
		if(result != null)
			new TestLogFactory().getLogger().add_name_value(instruction_output, "UseRepositoryUniqueId", result.toString());
		return result;
	}

	public Metadata getResult(String testdir, String step) throws XdsInternalException, MetadataException {
		OMElement result_ele = findResultInLog(step, testdir);
		if (result_ele == null)
			return null;
		OMElement ele = result_ele.getFirstElement();
		Metadata m = MetadataParser.parseNonSubmission(ele);
		if (debug) 
			System.out.println("getResult:" + 
					"\ntestdir = " + testdir + 
					"\nstep = " + step +
					"\nresult =  " + result_ele.getLocalName() +
					"\nele = " + ((ele == null) ? "null" : ele.getLocalName()) +
					"\nmetadata is " + m.getMetadataDescription());
		return m;
	}

	String getRepositoryUniqueId(Metadata m) throws XdsInternalException {
		repUniqueId =  null;
		for (OMElement eo : m.getExtrinsicObjects()) {
			String rui = m.getSlotValue(eo, "repositoryUniqueId", 0);
			if (debug) 
				System.out.println("eo = " + eo.getAttributeValue(MetadataSupport.id_qname) +
						" repositoryUniqueId = " + rui);
			if (rui == null || rui.equals(""))
				throw new XdsInternalException("RetrieveTransaction: getRepositoryUniqueId(): ExtrinsicObject " + 
						eo.getAttributeValue(MetadataSupport.id_qname) + 
				"does not have a repositoryUniqueId attribute");
			if (repUniqueId == null)
				repUniqueId = rui;
			else if (!rui.equals(repUniqueId)) {
				throw new XdsInternalException("RetrieveTransaction: getRepositoryUniqueId(): this metadata contains multiple repositorUniqueIds, this tool is not able to deal with this configuration");
			}
		}
		return repUniqueId;
	}

	String getHomeCommunityId(Metadata m) {
		homeCommunityId = null;

		for (OMElement ro : m.getAllObjects()) {
			homeCommunityId = m.getHome(ro);
			if (homeCommunityId != null && !homeCommunityId.equals(""))
				return homeCommunityId;
		}

		return homeCommunityId;
	}

	String getHomeCommunityId() {
		return homeCommunityId;
	}

	boolean bool(String value) {
		if (value == null)
			return false;
		return value.equalsIgnoreCase("true");
	}


	HashMap<String, String> compileUseXPath() throws XdsInternalException {
		// symbol => id_value
		//linkage = new HashMap<String, String>();

		OMElement metadata_ele = (m == null) ? null : m.getRoot();
		for (int i=0; i<use_xpath.size(); i++) {
			OMElement use = (OMElement) use_xpath.get(i);
			String testdir = use.getAttributeValue(new QName("testdir"));
			String step = use.getAttributeValue(new QName("step"));
			String xpath = use.getText();
			String file = use.getAttributeValue(new QName("file"));
			String symbol = use.getAttributeValue(new QName("symbol"));
			boolean base64decode = bool(use.getAttributeValue(new QName("decodebase64")));

			boolean is_testdir = 
				(testdir != null && !testdir.equals("") &&
						step != null && !step.equals("") &&
						xpath != null && !xpath.equals("") &&
						symbol != null && !symbol.equals(""));
			boolean is_mgmt =
				(file != null && !file.equals("") &&
						xpath != null && !xpath.equals("")	&&
						symbol != null && !symbol.equals(""));

			if (is_testdir == false && is_mgmt == false )
				throw new XdsInternalException(": <UseXPath element must contain of these sets of attributes:\n" +
						"testdir, step, symbol, with the body of the element holding the xpath OR\n" +
				"file, symbol, with the body of the element holding the xpath");
			try {
				if (is_testdir) {
					OMElement root = Parse.parse_xml_file(getLogFileName(testdir));
					List<OMElement> test_steps = MetadataSupport.decendentsWithLocalName(root, "TestStep");
					OMElement step_ele = null;
					for (OMElement test_step : test_steps) {
						String step_id = test_step.getAttributeValue(new QName("id"));
						if (step.equals(step_id)) {
							step_ele = test_step;
							break;
						}
					}
					if (step_ele == null)
						throw new XdsInternalException("Linkage:compileUseXPath(): Cannot find TestStep " + step + " in " + getLogFileName(testdir));

					AXIOMXPath xpathExpression = new AXIOMXPath (xpath);
					String result = xpathExpression.stringValueOf(step_ele);

					if (base64decode)
						result = Base64Coder.decodeString(result);

					addLinkage(symbol, result);
					if (metadata_ele != null)
						replace_string_in_text_and_attributes(metadata_ele, symbol, result);

				} else {
					if (file.contains("MGMT")) 
						file = file.replaceFirst("MGMT", testConfig.testmgmt_dir);
					OMElement root = Parse.parse_xml_file(file);
					AXIOMXPath xpathExpression = new AXIOMXPath (xpath);
					String result = xpathExpression.stringValueOf(root);

					if (base64decode)
						result = Base64Coder.decodeString(result);

					addLinkage(symbol, result);
					if (metadata_ele != null)
						replace_string_in_text_and_attributes(metadata_ele, symbol, result);
				}
			}
			catch (Exception e) {
				throw new XdsInternalException("Linkage:compileUseXPath(): problem compiling xpath expression\n" + xpath + "\nunable to access referenced data:\n" + e.getMessage() + "\n" +
						ExceptionUtil.exception_details(e));
			}
		}
		if (linkage != null)
			new TestLogFactory().getLogger().add_name_value(instruction_output, "UseXPath", Util.xmlizeHashMap(linkage));

		return linkage;
	}

	public HashMap<String, String> compileUseId() throws XdsInternalException,
	FactoryConfigurationError, XdsInternalException {
		// symbol => id_value
		//linkage = new HashMap<String, String>();
		OMElement metadata_ele = (m == null) ? null : m.getRoot();
		for (int i=0; i<use_id.size(); i++) {
			OMElement use = (OMElement) use_id.get(i);
			if (debug) {
				System.out.println("Compiling " + use);
			}
			String id = use.getAttributeValue(new QName("id"));
			String step_id = use.getAttributeValue(new QName("step"));
			String section_name = use.getAttributeValue(new QName("section"));
			String symbol = use.getAttributeValue(new QName("symbol"));
			String value = use.getAttributeValue(new QName("value"));
			String test_dir = use.getAttributeValue(new QName("testdir"));
			boolean by_value = false;

			if ( symbol != null && !symbol.equals("") &&
					value != null && !value.equals(""))
				by_value = true; // ok combination
			else
				if( 
						id == null || id.equals("") ||
						step_id == null || step_id.equals("") ||
						section_name == null || section_name.equals("") ||
						symbol == null || symbol.equals("")
				) {
					throw new XdsInternalException(": <UseId element must have id, type, and symbol attributes" +
							"\n OR  symbol and value must be set programatically "+
							"\nid = " + id +
							"\nstep = " + step_id +
							"\nsection = " + section_name +
							"\nsymbol = " + symbol +
							"\nvalue = " + value +
							"\ntestdir = " + test_dir);
				}

			if (debug)
				System.out.println("by value is " + by_value);

			OMElement transaction_output;
			if (by_value) {
				if (debug) 
					System.out.println("addLinkage by value symbol=" + symbol + "  value=" + value); 
				addLinkage(symbol, value);
				if (metadata_ele == null) throw new XdsInternalException("metadata_ele is null");
				if (metadata_ele != null)
					replace_string_in_text_and_attributes(metadata_ele, symbol, value);
			}
			else {
				if (test_dir != null && test_dir.length() > 0) {
					// look in previous log file
					OMElement log = getLogContents(test_dir);
					transaction_output = find_instruction_output(log, step_id, null);
					if (transaction_output == null) {
						throw new XdsInternalException("Linkage:CompileUseId(): " + format_section_and_step(step_id, section_name) +  
								" Transaction with step_id " + step_id + " cannot be found in log file " + getLogFileName(test_dir));
					}
				} else {
					// look in this log file
					transaction_output = null;
					if (instruction_output != null)
						transaction_output = find_previous_instruction_output(instruction_output, step_id, null);
					if (transaction_output == null) {
						throw new XdsInternalException("Linkage compiler: cannot find transaction output in this log file for " + format_section_and_step(step_id, section_name));
					}
				}

				OMElement section = MetadataSupport.firstChildWithLocalName(transaction_output, section_name); 
				if (section == null)
					throw new XdsInternalException(format_section_and_step(step_id, section_name) + " not found in any previous step");

				if (debug)
					System.out.println("section is " + section);

				boolean foundit = false;
				for (OMElement assign : MetadataSupport.childrenWithLocalName(section, "Assign")) {
					String symbol_value = assign.getAttributeValue(new QName("symbol"));
					String id_value = assign.getAttributeValue(new QName("id"));
					if (debug) 
						System.out.println("Assign symbol=" + symbol_value + "  value=" + id_value + " looking for id=" + id);

					if (symbol_value == null || symbol_value.equals(""))
						throw new XdsInternalException(format_section_and_step(step_id, section_name) + " empty assign section (no symbol attribute)");
					if (id_value == null || id_value.equals(""))
						throw new XdsInternalException(format_section_and_step(step_id, section_name) + " empty assign section (no id attribute)");
					if (! symbol_value.equals(id))
						continue;
					if (section.equals("AssignedPatientId")) {
						new TestMgmt(testConfig).assignPatientId(m, id_value);
					} else {
						if (debug) 
							System.out.println("addLinkage symbol=" + symbol + "  value=" + value);					
						addLinkage(symbol, id_value);
						foundit = true;
						if (metadata_ele != null)
							replace_string_in_text_and_attributes(metadata_ele, symbol, id_value);
					}
				}
				if (!foundit)
					throw new XdsInternalException("Linkage Compiler: cannot find definition of id " + id + 
							" from " + test_dir + " step " + step_id + " section " + section_name);
			}

		}
		if (linkage != null)
			new TestLogFactory().getLogger().add_name_value(instruction_output, "UseId", Util.xmlizeHashMap(linkage));
		return linkage;
	}

	public void compileLinkage() throws XdsInternalException {

		OMElement root = (m == null) ? null : m.getRoot();

		apply(root);
	}

	public void compileLinkage(OMElement root) throws XdsInternalException {
		apply(root);
	}

	public void apply(OMElement root) throws XdsInternalException {
		for (String key : linkage.keySet()) {
			String value = linkage.get(key);
			replace_string_in_text_and_attributes(root, key, value);
		}
	}

	public HashMap<String, String> compileUseObjectRef() throws XdsInternalException,
	FactoryConfigurationError, MetadataException {
		// symbol => id_value
		//linkage = new HashMap<String, String>();
		int use_id;  // just to find references
		OMElement metadata_ele = (m == null) ? null : m.getRoot();
		for (int i=0; i<use_object_ref.size(); i++) {
			OMElement use = (OMElement) use_object_ref.get(i);
			String step_id = use.getAttributeValue(new QName("step"));  
			String index = use.getAttributeValue(new QName("index"));
			String symbol = use.getAttributeValue(new QName("symbol")); 
			String test_dir = use.getAttributeValue(new QName("testdir"));  

			if ( step_id != null && !step_id.equals("") && 
					index != null && !index.equals("") &&
					symbol != null && !symbol.equals("") )
				;
			else
				throw new XdsInternalException("<UseObjectRef element must have testdir, step, index, and symbol attributes" +
						"\nindex = " + index +
						"\nstep = " + step_id +
						"\nsymbol = " + symbol +
						"\ntestdir = " + test_dir);
			int index_i = Integer.parseInt(index);
			if (index_i < 0)
				throw new XdsInternalException("<UseObjectRef element has index less than zero [" + index + "] - invalid index");

			OMElement transaction_output = find_transaction_in_log(step_id, test_dir);

			OMElement result = MetadataSupport.firstChildWithLocalName(transaction_output, "Result");
			if (result == null)
				throw new XdsInternalException("Cannot find Result section in log of step " + step_id + " in test directory " + test_dir);

			Metadata m = MetadataParser.parseNonSubmission(result.getFirstElement());

			List<OMElement> object_refs = m.getObjectRefs();
			if (index_i >= object_refs.size())
				throw new XdsInternalException("<UseObjectRef requests index of " + index_i + " but query retured only [" + object_refs.size() + "] ObjectRefs");

			String value = object_refs.get(index_i).getAttributeValue(MetadataSupport.id_qname);
			addLinkage(symbol, value);
			if (metadata_ele != null)
				replace_string_in_text_and_attributes(metadata_ele, symbol, value);
		}
		if(linkage != null)
			new TestLogFactory().getLogger().add_name_value(instruction_output, "UseObjectRef", Util.xmlizeHashMap(linkage));
		return linkage;
	}

	private OMElement find_transaction_in_log(String step_id, String test_dir)
	throws FactoryConfigurationError, XdsInternalException,
	XdsInternalException {
		OMElement transaction_output;
		if (test_dir != null && test_dir.length() > 0) {
			// look in previous log file
			OMElement log = getLogContents(test_dir);
			transaction_output = find_instruction_output(log, step_id, null);
			if (transaction_output == null) {
				throw new XdsInternalException(format_section_and_step(step_id, "any") +  
						" Transaction with step_id " + step_id + " cannot be found in log file " + getLogFileName(test_dir));
			}
		} else {
			// look in this log file
			transaction_output = null;
			if (instruction_output != null)
				transaction_output = find_previous_instruction_output(instruction_output, step_id, null);
			if (transaction_output == null) {
				throw new XdsInternalException(format_section_and_step(step_id, "any") + " Transaction not found in any previous step");
			}
		}
		return transaction_output ;
	}

	public OMElement findResultInLog(String step_id, String test_dir) throws FactoryConfigurationError, XdsInternalException,
	XdsInternalException {

		OMElement transaction_output = find_transaction_in_log(step_id, test_dir);

		if (transaction_output == null) 
			throw new XdsInternalException("Linkage:findResultInLog(): Cannot find *Transaction in log of step " + step_id + " in " + test_dir + "/log.xml");

		OMElement result = MetadataSupport.firstChildWithLocalName(transaction_output, "Result");
		if (result == null) 
			throw new XdsInternalException("Linkage:findResultInLog(): Cannot find Result in log of step " + step_id + " in " + test_dir + "/log.xml");

		if (debug) 
			System.out.println("findResultInLog\n" + result.toString());

		return result;
	}

	protected OMElement find_previous_instruction_output(OMElement this_instruction_output, String target_test_step_id, String target_transaction_type) throws XdsInternalException {
		// example target_transaction_type is "RegisterTransaction"
		//System.out.println("Searching for step id=" + target_test_step_id + " and transaction type " + target_transaction_type);
		OMElement this_step_output = (OMElement) this_instruction_output.getParent();
		OMElement step_output = null;
		step_output = (OMElement) this_step_output.getPreviousOMSibling();
		while (step_output != null) {
			String step_output_id = step_output.getAttributeValue(new QName("id"));
			if (step_output_id != null && step_output_id.equals(target_test_step_id)) {
				OMElement transaction_output = (target_transaction_type == null)  
				? MetadataSupport.firstChildWithLocalNameEndingWith(step_output, "Transaction")
						: MetadataSupport.firstChildWithLocalName(step_output, target_transaction_type) ;
				return transaction_output;
			}
			step_output = (OMElement) step_output.getPreviousOMSibling();
		}
		return null;
	}




}
