package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.registrymetadata.IdParser;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.testenginelogging.LogFileContentBuilder;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.SectionLogMapDTO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.NoMetadataException;
import gov.nist.toolkit.xdsexception.SchemaValidationException;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.util.*;

public abstract class BasicTransaction  {
	protected OMElement instruction;
	protected OMElement instruction_output;
	PlanContext planContext = null;
	protected StepContext s_ctx;
	OmLogger testLog = new TestLogFactory().getLogger();

	boolean step_failure = false;
	boolean no_convert = false;
	boolean isSaml = false ;
	public boolean parse_metadata = true;
	// metadata building linkage
	protected ArrayList<OMElement> use_id;
	protected ArrayList<OMElement> use_xpath ;
	protected ArrayList<OMElement> use_object_ref;
	protected ArrayList<OMElement> use_repository_unique_id;
	// assertion linkage
	protected ArrayList<OMElement> data_refs;
	protected ArrayList<OMElement> assertionEleList;

	static public final short xds_none = 0;
	static public final short xds_a = 1 ;
	static public final short xds_b = 2;
	short xds_version = BasicTransaction.xds_none;
	protected String endpoint = null;
	HashMap<String, String> local_linkage_data = new HashMap<>() ;  // dangerous, most of linkage kept inside Linkage class
	Linkage linkage = null;
	protected String metadata_filename = null;

	protected boolean assign_uuids = false;
	protected boolean assign_uids = true;
	protected String no_assign_uid_to = null;
	protected boolean assign_patient_id = true;
	protected boolean soap_1_2 = true;
	protected boolean async = false;
	protected boolean isStableOrODDE = false;
	/**
	HttpTransaction uses a separate Body.txt file. In this case we need to tell not run reportManagerPreRun since the subclass will run the parameter replacement on its own.
	 */
	boolean noReportManagerPreRun = false;
	boolean noMetadataProcessing = false;  // example Retrieve request - no metadata to process
	boolean useMtom;
	boolean useAddressing;
	boolean isSQ;
	public boolean defaultEndpointProcessing = true;

	protected String repositoryUniqueId = null;
	private final static Logger logger = Logger.getLogger(BasicTransaction.class);
	Map<String, String> nameUuidMap = null;
	private Soap soap;

	//	Metadata metadata = null;
	OMElement request_element;
	//	OMElement submission = null;

	List<OMElement> additionalHeaders = null;
	List<OMElement> wsSecHeaders = null;
	ReportManager reportManager = null;
	UseReportManager useReportManager = null;
	TestConfig testConfig;

	TransactionSettings transactionSettings = null;

	protected abstract void run(OMElement request) throws Exception;
	protected abstract void parseInstruction(OMElement part) throws XdsInternalException, MetadataException;
	protected abstract String getRequestAction();
	protected abstract String getBasicTransactionName(); // trans name without possible .as suffix

	public void setTransactionSettings(TransactionSettings ts) {
		this.transactionSettings = ts;
	}

	public StepContext getStepContext() {
		return s_ctx;
	}

	public UseReportManager getUseReportManager() { return useReportManager; }

	public void setUseReportManager(UseReportManager m) { useReportManager = m; }

	public Map<String, String> getExternalLinkage() {
		return planContext.getExtraLinkage();
	}

	public ReportManager getReportManager() {
		if (reportManager == null)
			reportManager = new ReportManager(testConfig);
		return reportManager;
	}

	void applyTransactionSettings() {
		if (transactionSettings == null)
			return;
		if (transactionSettings.assignPatientId != null)
			assign_patient_id = transactionSettings.assignPatientId;
		async = false;
		if (transactionSettings.siteSpec != null)     // null for Direct
			async = transactionSettings.siteSpec.isAsync;
		if (transactionSettings.patientId != null)
			planContext.setPatientId(transactionSettings.patientId);
		if (transactionSettings.altPatientId != null)
			planContext.setAltPatientId(transactionSettings.altPatientId);
	}

	public boolean getUseMtom() { return useMtom; }
	public void setUseMtom(boolean value) { useMtom = value; }
	public OmLogger getTestLog() { return testLog; }
	public void setTestConfig(TestConfig config) {
		testConfig = config;
	}
	public TestConfig getTestConfig() { return testConfig; }

	public void setPlanContext(PlanContext pc) {
		planContext = pc;
	}

	protected StepContext getStep() { return s_ctx; }
	protected PlanContext getPlan() { return planContext; }

	public void setXdsVersion(short version) {
		xds_version = version;
	}

	public void setParseMetadata(boolean parse) {
		parse_metadata = parse;
	}

	protected boolean isB() {
		return xds_version == BasicTransaction.xds_b;
	}

	public void doRun() throws Exception {

		try {
			Iterator<OMElement> elements = instruction.getChildElements();
			while (elements.hasNext()) {
				OMElement part = (OMElement) elements.next();
				parseInstruction(part);
			}

			applyTransactionSettings();


			String trans = getBasicTransactionName();
			if (trans == null)
				fatal("Internal error: No transaction name declared");

			if (async)
				xds_version = BasicTransaction.xds_b;

			if (trans.equals("sq") || trans.equals("pr") || trans.equals("r")) {
				//			if (async)
				//				trans = trans + ".as";
				//			else
				if (isB())
					trans = trans + ".b";
				else
					trans = trans + ".a";
			}
			//		else if (async)
			//			trans = trans + ".as";

			TransactionType ttype = TransactionType.find(trans);

//		if (ttype == null)
//			fatal("Do not understand transaction type " + trans);

			if (defaultEndpointProcessing && ttype != null)
				parseEndpoint(ttype);

			Metadata metadata = prepareMetadata();
			if (metadata != null)
				request_element = metadata.getRoot();

			//		reportManagerPreRun(request_element);  // must run before prepareMetadata (assign uuids)

			run(request_element);

			if (s_ctx.getExpectedStatus().size()>0 && !s_ctx.getExpectedStatus().get(0).isFault())
				reportManagerPostRun();
		} catch (Exception e) {
			s_ctx.set_error("Internal Error: " + ExceptionUtil.exception_details(e));
			step_failure = true;
		}
	}

	protected void reportManagerPostRun() throws XdsInternalException {
		SectionLogMapDTO sectionLogs = getPlan().getPreviousSectionLogs();

		try {
			sectionLogs.remove("THIS");
		} catch (Exception e) {}

		if ( ! step_failure ) {

			if (reportManager != null ) {
                //reportStepParameters();

				reportManager.setXML(instruction_output);
                logger.info("Reporting run parameters: " + getPlan().getExtraLinkage());
                reportManager.report(getPlan().getExtraLinkage());
				reportManager.generate();
                // report run parameters as Reports

				testLog.add_name_value(instruction_output, reportManager.toXML());

            }

		}
	}

	protected void reportManagerPreRun(OMElement metadata_element) throws XdsInternalException,
	XdsInternalException {

		// Extra linkage is a parameter map passed in from the UI
        compileExtraLinkage(metadata_element);

		if (useReportManager != null) {

			SectionLogMapDTO sectionLogs = getPlan().getPreviousSectionLogs();

			// add in current section log so we can reference ourself
			try {
				sectionLogs.put("THIS", new LogFileContentBuilder().build(getPlan().getLog(), true /* incomplete is ok */));
			} catch (Exception e) {
				e.printStackTrace();
			}

			//TestSections dependencies = useReportManager.getTestSectionsReferencedInUseReports();

			try {
				useReportManager.loadPriorTestSections(transactionSettings, testConfig);
			} catch (Exception e) {

				// because useReportManager.resolve below will take care of many problems
//				fatal("UseReportManager failed to load necessary prior-test log files", e);
//				return;
			}


			useReportManager.resolve(sectionLogs);

			useReportManager.apply(metadata_element);
			testLog.add_name_value(instruction_output, useReportManager.toXML());
		}
	}

	private void addStandardLinkage(Metadata metadata) throws XdsInternalException {
		linkage = new Linkage(testConfig, instruction_output, metadata);
		linkage.addLinkage("$now$", new Hl7Date().now());
		linkage.addLinkage("$lastyear$", new Hl7Date().lastyear());
		linkage.addLinkage("$AlternatePatientId$", new PatientIdAllocator(testConfig).getAltPatientId());
		linkage.compileLinkage();
	}

	protected void applyLinkage(OMElement metadata) throws XdsInternalException {
		linkage = new Linkage(testConfig, instruction_output);
		linkage.addLinkage("$now$", new Hl7Date().now());
		linkage.addLinkage("$lastyear$", new Hl7Date().lastyear());
		linkage.apply(metadata);
	}

	public String toString() {
		String exceptionString = "";

		try {
			throw new Exception("foo");
		} catch (Exception e) { exceptionString = ExceptionUtil.exception_local_stack(e); }
		return new StringBuffer()
//		.append("Called From:\n")
//		.append(exceptionString)
		.append("BasicTransaction\n")
		.append("Step = ").append(s_ctx.getId()).append("\n")
		.append("transaction = ").append(this.getClass().getName()).append("\n")
		.append("step_failure = ").append(step_failure).append("\n")
		.append("parse_metadata = ").append(parse_metadata).append("\n")
		.append("xds_version = ").append(xdsVersionName()).append("\n")
		.append("use_id = ").append((use_id == null) ? null : use_id.toString())
		.append("use_xpath = ").append((use_xpath == null) ? null : use_xpath.toString())
		.append("use_object_ref = ").append((use_object_ref == null) ? null : use_object_ref.toString())
		.append("use_repository_unique_id = ").append((use_repository_unique_id == null) ? null : use_repository_unique_id.toString())
		.append("data_refs = ").append((data_refs == null) ? null : data_refs.toString())
		.append("assertionEleList = ").append((assertionEleList == null) ? null : assertionEleList.toString())
		.append("endpoint = ").append(endpoint).append("\n")
		.append("linkage = ").append((local_linkage_data == null) ? null : local_linkage_data.toString())
		.append("metadata_filename = ").append(metadata_filename).append("\n")
		.append("assign_uuids = ").append(assign_uuids).append("\n")
		.append("assign_uids = ").append(assign_uids).append("\n")
		.append("no_assign_uid_to = ").append(no_assign_uid_to).append("\n")
		.append("assign_patient_id = ").append(assign_patient_id).append("\n")
		.append("soap_1_2 = ").append(soap_1_2).append("\n")
		.append("repositoryUniqueId = ").append(repositoryUniqueId).append("\n")
		.append("nameUuidMap = ").append(nameUuidMap).append("\n")

		.toString();


	}

	public String xdsVersionName() {
		if (xds_version == BasicTransaction.xds_none)
			return "None";
		if (xds_version == BasicTransaction.xds_a)
			return "XDS.a";
		if (xds_version == BasicTransaction.xds_b)
			return "XDS.b";
		return "Unknown";
	}


	// to force sub-classes to use following constructor
	private BasicTransaction() {  }

	protected BasicTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		this.s_ctx = s_ctx;
		this.instruction = instruction;
		this.instruction_output = instruction_output;

		use_id = new ArrayList<OMElement>();
		use_xpath = new ArrayList<OMElement>();
		use_object_ref = new ArrayList<OMElement>();
		use_repository_unique_id = new ArrayList<OMElement>();
		data_refs = new ArrayList<OMElement>();
		assertionEleList = new ArrayList<OMElement>();
		local_linkage_data = new HashMap<String, String>();
		isSQ = false;

    }

	String xds_version_name() {
		if (xds_version == xds_a)
			return "XDS.a";
		if (xds_version == xds_b)
			return "XDS.b";
		return "Unknown";
	}

	void failed() {
		step_failure = true;
	}

	void validate_registry_response_in_soap(OMElement env, String topElementName, int metadata_type) throws XdsInternalException, MetadataValidationException, MetadataException {
		if (!env.getLocalName().equals("Envelope"))
			throw new XdsInternalException("Expected 'Envelope' but found " + env.getLocalName() + " instead");
		OMElement hdr = env.getFirstElement();
		if (hdr == null)
			throw new XdsInternalException("Expected 'Header' but found nothing instead");
		if (!hdr.getLocalName().equals("Header"))
			throw new XdsInternalException("Expected 'Header' but found " + hdr.getLocalName() + " instead");
		Object next = hdr.getNextOMSibling();
		if (!(next instanceof OMElement))
			throw new XdsInternalException("Body not of type OMElement, instead found " + ((next == null) ? "null" : next.getClass().getName()	));
		OMElement body = (OMElement) next;
		if (body == null)
			throw new XdsInternalException("Expected 'Body' but found nothing instead");
		if (!body.getLocalName().equals("Body"))
			throw new XdsInternalException("Expected 'Body' but found " + body.getLocalName() + " instead");
		validate_registry_response(body.getFirstElement(), topElementName, metadata_type);

	}

	void validate_registry_response(OMElement result, String topElementName, int metadata_type) throws XdsInternalException, MetadataValidationException, MetadataException {
		// metadata type was MetadataTypes.METADATA_TYPE_PR
		validate_registry_response_no_set_status(result, topElementName, metadata_type);

		add_step_status_to_output();
	}

	void validate_registry_response_no_set_status(OMElement registry_result, String topElementName, int metadata_type) throws XdsInternalException, MetadataValidationException, MetadataException {
		if (registry_result == null) {
			s_ctx.set_error("No Result message");
			step_failure = true;
			return;
		}

		if (topElementName != null && registry_result != null) {
			String topElementLocalName = registry_result.getLocalName();
			if (!"Fault".equals(topElementLocalName) && !topElementName.equals(topElementLocalName)) {
				s_ctx.set_error("Message top level element must be " + topElementName + " found " + registry_result.getLocalName() + " instead");
				step_failure = true;
				return;
			}
		}

		RegistryResponseParser registry_response = new RegistryResponseParser(registry_result);

		String status = registry_response.get_registry_response_status();

		if (!"Fault".equals(status))
			validateSchema(registry_result, metadata_type);

		ArrayList<String> returned_code_contexts = registry_response.get_error_code_contexts();

		RegistryErrorListGenerator rel  = null;
		ValidationContext vc = getValidationContextFromTransactionName();
		vc.isResponse = true;
		vc.isStableOrODDE = isStableOrODDE;
		try {
            SecurityParams sp = s_ctx.getTransactionSettings().securityParams;
			logger.info("Codes file is " + sp.getCodesFile());
			vc.setCodesFilename(this.s_ctx.getTransactionSettings().securityParams.getCodesFile().toString());
		} catch (Exception e) {}
		try {
			rel = RegistryUtility.metadata_validator(MetadataParser.parseNonSubmission(registry_result), vc);
		} catch (NoMetadataException e) {
			// not all responses contain metadata
			rel = new RegistryErrorListGenerator((xds_version == xds_a ? RegistryErrorListGenerator.version_2 : RegistryErrorListGenerator.version_3));
		}

		ArrayList<String> validatorErrors = new RegistryResponseParser(rel.getRegistryErrorList()).get_error_code_contexts();
		returned_code_contexts.addAll(validatorErrors);

		eval_expected_status(status, returned_code_contexts);
		if (step_failure == false && validatorErrors.size() != 0) {
			StringBuilder msg = new StringBuilder();
			for (int i=0; i<validatorErrors.size(); i++) {
				msg.append(validatorErrors.get(i));
				msg.append("\n");
			}
			s_ctx.set_error(msg.toString());
			step_failure = true;
		}

		String expectedErrorCode = s_ctx.getExpectedErrorCode();
		if (expectedErrorCode != null && !expectedErrorCode.equals("")) {
			List<String> errorCodes = registry_response.get_error_codes();
			if ( ! errorCodes.contains(expectedErrorCode)) {
				s_ctx.set_error("Expected errorCode of " + expectedErrorCode + "\nDid getRetrievedDocumentsModel errorCodes of " +
						errorCodes);
				step_failure = true;
			}
		}
	}

	ValidationContext getValidationContextFromTransactionName() {
		ValidationContext vc = DefaultValidationContextFactory.validationContext();

		String tname = getBasicTransactionName();

		if ("sq".equals(tname)) vc.isSQ = true;
		if ("pr".equals(tname)) vc.isPnR = true;
		if ("r".equals(tname)) vc.isR = true;
		if ("rodde".equals(tname)) vc.isRODDE = true;

		return vc;
	}

	protected void validateSchema(OMElement registry_result, int metadata_type)
	throws XdsInternalException {
		// schema validate response
		String schema_results = "";
		try {
			RegistryUtility.schema_validate_local(registry_result, metadata_type);
		} catch (SchemaValidationException e) {
			s_ctx.set_error("Schema validation threw exception: " + e.getMessage());
			step_failure = true;
		}
	}

	String getExpectedStatusString() {
		return Arrays.toString(s_ctx.getExpectedStatus().toArray());
	}

	void eval_expected_status(String status,
			ArrayList<String> returned_code_contexts) throws XdsInternalException {
		TransactionStatus currentStatus = new TransactionStatus(status);
		List<TransactionStatus> expectedStatus = s_ctx.getExpectedStatus();
		//		if (!expected_status) {
		//			step_failure = false;
		//			this.s_ctx.resetStatus();
		//		}
		if (xds_version == xds_a && isSQ == false) {
			if (currentStatus != expectedStatus) {
				s_ctx.set_error("Status is " + status + ", expected status is " + getExpectedStatusString());
				step_failure = true;
			}
		} else {

			if (currentStatus.isFault() && (expectedStatus.size()>0) && expectedStatus.get(0).isFault()) {
				// Originally set to true in the BasicTransaction
				// 			s_ctx.set_error("Internal Error: " + ExceptionUtil.exception_details(e));
				// Since Fault is Expected Status, it is not a step failure.
				s_ctx.resetStatus();
				step_failure = false;
				return;
			}

			StringBuffer expectedStatusSb = new StringBuffer();
			int counter=1;
			for (TransactionStatus ts : expectedStatus) {

				expectedStatusSb.append(ts.getNamespace());
				if (expectedStatus.size()>1 && counter<expectedStatus.size())
					expectedStatusSb.append(" OR ");
				counter++;
			}


			if ( ! currentStatus.isNamespaceOk()) {
				s_ctx.set_error("Status is " + status + " , expected status is " + expectedStatusSb);
				step_failure = true;
			} else {

				boolean foundAcceptableStatus = false;
				for (TransactionStatus ts : expectedStatus) {
					if ( currentStatus.equals(ts)) {
						foundAcceptableStatus = true;
					}
				}

				if (!foundAcceptableStatus) {
					s_ctx.set_error("Status is " + status + ", expected status is " + expectedStatusSb.toString());
					step_failure = true;
				}
			}
		}

		// if expected error message specified then expected status must be Failure
		boolean hasExpectedErrorMessage = !s_ctx.getExpectedErrorMessage().equals("");
		boolean isExpectingFailure = expectedStatus.size()==1 && expectedStatus.get(0).isFailure();
		if (  hasExpectedErrorMessage && !isExpectingFailure) {
			fatal("ExpectedErrorMessage specified but ExpectedStatus is Success. This does not make sense");
		}

		// check that the error message matches expected
		boolean error_message_found = false;
		if (  !s_ctx.getExpectedErrorMessage().equals("") ) {
			for (int i=0; i<returned_code_contexts.size(); i++) {
				String message = returned_code_contexts.get(i);
				if (message.indexOf(s_ctx.getExpectedErrorMessage()) != -1) {
					error_message_found = true;
				}
			}
			if (!error_message_found) {
				s_ctx.set_error("Did not find expected string in error messages: " + s_ctx.getExpectedErrorMessage() + "\n");
				step_failure = true;
			}
		}
	}


	void add_step_status_to_output() {
		s_ctx.setStatusInOutput(!step_failure);
		if (step_failure)
			testLog.add_name_value(instruction_output, "StepStatus", "Failure");
		else
			testLog.add_name_value(instruction_output, "StepStatus", "Success");
	}


	protected OMElement generate_xml(String root_name, Map<String, String> map) {
		OMElement root = om_factory().createOMElement(root_name, null);
		for (Iterator it =map.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			String value = (String) map.get(key);
			OMElement new_ele = om_factory().createOMElement("Assign", null);
			new_ele.addAttribute("symbol", key, null);
			new_ele.addAttribute("id", value, null);
			root.addChild(new_ele);
		}
		return root;
	}

	protected OMElement generate_xml(String root_name, String value) {
		OMElement root = om_factory().createOMElement(root_name, null);
		OMElement new_ele = om_factory().createOMElement("Assign", null);
		new_ele.addAttribute("id", value, null);
		root.addChild(new_ele);
		return root;
	}


	protected OMFactory om_factory() {
		return OMAbstractFactory.getOMFactory();
	}




	void print_step_history(OMElement this_instruction_output) {
		System.out.println("Step History:");
		OMElement step_output = (OMElement) this_instruction_output.getParent();
		while (step_output != null) {
			String id = step_output.getAttributeValue(new QName("id"));
			System.out.println("id = " + id);
			step_output = (OMElement) step_output.getPreviousOMSibling();
		}
	}

	void addToLinkage(HashMap<String, String> in) {
		for (Iterator<String> it=in.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			String value = in.get(key);
			local_linkage_data.put(key, value);
		}
	}


	protected void compileUseRepositoryUniqueId(Metadata m, ArrayList<OMElement> useInputMetadata) throws XdsInternalException, MetadataException, FactoryConfigurationError {
		Linkage l = new Linkage(testConfig, instruction_output, m);
		l.setUseRepositoryUniqueId(useInputMetadata);
		try {
			HashMap<String, String> in = l.compile();
			addToLinkage(in);
			repositoryUniqueId = l.getRepositoryUniqueId();
		}
		catch (XdsInternalException e) {
			throw new XdsInternalException(ExceptionUtil.exception_details(e, "Step is " + s_ctx.get("step_id")));
		}
	}



	protected void compileUseIdLinkage(Metadata m, ArrayList use_id) throws XdsInternalException, MetadataException, FactoryConfigurationError {
		Linkage l = new Linkage(testConfig, instruction_output, m, use_id);
		try {
			addToLinkage(l.compile());
		}
		catch (XdsInternalException e) {
			throw new XdsInternalException(s_ctx.get("step_id") + ": " + e.getMessage(),e);
		}

	}

	protected void compileUseObjectRefLinkage(Metadata m, ArrayList use_object_ref) throws XdsInternalException, MetadataException, FactoryConfigurationError {
		Linkage l = new Linkage(testConfig, instruction_output, m);
		l.setUseObjectRef(use_object_ref);
		try {
			addToLinkage(l.compile());
		}
		catch (XdsInternalException e) {
			throw new XdsInternalException(s_ctx.get("step_id") + ": " + e.getMessage());
		}

	}

	protected void compileUseXPathLinkage(Metadata m, ArrayList use_xpath) throws XdsInternalException, MetadataException, FactoryConfigurationError {
		Linkage l = new Linkage(testConfig, instruction_output, m);
		l.setUseXPath(use_xpath);
		try {
			HashMap<String, String> link = l.compile();
			addToLinkage(link);
			s_ctx.set("UseXPath", link.toString());
		}
		catch (XdsInternalException e) {
			throw new XdsInternalException(s_ctx.get("step_id") + ": " + e.getMessage() + ExceptionUtil.exception_details(e));
		}

	}

	protected void parseEndpoint(TransactionType trans) throws Exception {
		endpoint = this.s_ctx.getRegistryEndpoint();   // this is busted, always returns null
		if (endpoint == null || endpoint.equals("") || testConfig.endpointOverride) {			//boolean async = false;
			if (testConfig.verbose)
				System.out.println("endpoint coming from actors.xml");
			if (trans.getCode().endsWith(".as")) {
				xds_version = xds_b;
			}
			if (testConfig.site == null) {
				logger.error(ExceptionUtil.here("testConfig.site is null"));
				throw new XdsInternalException("BasicTransaction#parseEndpoint: TestConfig.site not configured");
			}
			if (trans.usesTraditionalTransactions()) {
				// otherwise handled elsewhere
				endpoint = testConfig.site.getEndpoint(trans, testConfig.secure, async);
				if (endpoint == null || endpoint.equals(""))
					fatal("No endpoint specified for transaction " + trans + " and XDS version " + xds_version_name() +
							" and secure = " + testConfig.secure +
							" on site " + testConfig.site.getSiteName() + "\nactor config is " + testConfig.site.toString());
				testLog.add_name_value(instruction_output, "Endpoint", endpoint);
			}
		} else {
			if (testConfig.verbose)
				System.out.println("endpoint coming from testplan.xml");
		}
		logger.info("Transaction = " + trans + " Endpoint = " + endpoint);
		showEndpoint();
	}

	void showEndpoint() {
		System.out.println("        Endpoint = " + endpoint);
	}

	protected void parseRepEndpoint(String repositoryUniqueId, boolean isSecure) throws Exception {
		if (endpoint == null || endpoint.equals("")) {
			if (s_ctx.getPlan().getRegistryEndpoint() != null)
				endpoint = s_ctx.getPlan().getRegistryEndpoint();
			else {
				try {
					// check current site first before looking at all the rest
					endpoint = testConfig.site.getRetrieveEndpoint(repositoryUniqueId, isSecure, async);
				} catch (Exception e) {

				}
				if (endpoint == null) {
					try {
						endpoint = testConfig.allRepositoriesSite.getRetrieveEndpoint(repositoryUniqueId, isSecure, async);
						//					endpoint = testConfig.site.getRetrieveEndpoint(repositoryUniqueId, isSecure, async);
					} catch (XdsInternalException e) {
						if (planContext.getDefaultRegistryEndpoint() != null)
							endpoint = planContext.getDefaultRegistryEndpoint();
						else
							fatal(e.getMessage());
					}
				}
			}
		}
		testLog.add_name_value(instruction_output, "Endpoint", endpoint);
		showEndpoint();
	}

	protected void parseGatewayEndpoint(String home, boolean isSecure) throws Exception {
		if (endpoint == null || endpoint.equals("")) {
			if (s_ctx.getPlan().getRegistryEndpoint() != null)
				endpoint = s_ctx.getPlan().getRegistryEndpoint();
			else
				try {
					endpoint = testConfig.site.getEndpoint(TransactionType.XC_RETRIEVE, isSecure, async);
//					endpoint = testConfig.site.getXRetrieve(home, isSecure, async);
				} catch (XdsInternalException e) {
					fatal(e.getMessage());
				}
		}
		testLog.add_name_value(instruction_output, "Endpoint", endpoint);
		showEndpoint();
	}

	protected void parseIGREndpoint(String home, boolean isSecure) throws Exception {
		if (endpoint == null || endpoint.equals("")) {
			if (s_ctx.getPlan().getRegistryEndpoint() != null)
				endpoint = s_ctx.getPlan().getRegistryEndpoint();
			else
				try {
					endpoint = testConfig.site.getEndpoint(TransactionType.IG_RETRIEVE, isSecure, async);
//					endpoint = testConfig.site.getIGRetrieve(home, isSecure, async);
				} catch (XdsInternalException e) {
					fatal(ExceptionUtil.exception_details(e, 5));
				}
		}
		testLog.add_name_value(instruction_output, "Endpoint", endpoint);
		showEndpoint();
	}

	protected void parseIDSEndpoint(String home, TransactionType transactionType, boolean isSecure) throws Exception {
		if (endpoint == null || endpoint.equals("")) {
			if (s_ctx.getPlan().getRegistryEndpoint() != null)
				endpoint = s_ctx.getPlan().getRegistryEndpoint();
			else
				try {
					endpoint = testConfig.site.getEndpoint(transactionType, isSecure, async);
//					endpoint = testConfig.site.getIGRetrieve(home, isSecure, async);
				} catch (XdsInternalException e) {
					fatal(ExceptionUtil.exception_details(e, 5));
				}
		}
		testLog.add_name_value(instruction_output, "Endpoint", endpoint);
		showEndpoint();
	}

	List<String> failMsgs = null;

	public void fail(String msg) throws XdsInternalException {
		failMsgs = asList(msg);
		failed();
		s_ctx.set_error(msg);
	}

    public void fail(List<String> msgs) throws XdsInternalException {
        failMsgs = msgs;
        failed();
        for (String x : msgs) s_ctx.set_error(x);
    }

    String asString(List<String> strs) {
        StringBuilder buf = new StringBuilder();
        for (String x : strs) buf.append(x).append("\n");
        return buf.toString();
    }

    List<String> asList(String str) {
        List<String> lst = new ArrayList<>();
        lst.add(str);
        return lst;
    }

	public String getFail() {
		if (failMsgs == null) return null;
		return asString(failMsgs);
	}

	protected void fatal(String msg) throws XdsInternalException {
		throw new XdsInternalException(msg);
	}

	protected void fatal(String msg, Exception e) throws XdsInternalException {
		throw new XdsInternalException(msg, e);
	}


	protected void log_metadata(OMElement submission) throws XdsInternalException {
		testLog.add_name_value(	instruction_output,
				"InputMetadata", Util.deep_copy(submission));
	}

	Metadata prepareMetadata() throws XdsInternalException, XdsInternalException, MetadataException, FactoryConfigurationError {
		Metadata metadata = null;
        logger.debug("metadata_filename is " + metadata_filename);
        if (metadata_filename != null && !metadata_filename.equals(""))
            request_element = Util.parse_xml(new File(metadata_filename));

//        if (request_element == null)
//            fatal("BasicTransaction:prepare_metadata(): metadata_element is null");
		if (isNoMetadataProcessing()) {
			if (!isNoReportManagerPreRun())
				reportManagerPreRun(request_element);  // must run before prepareMetadata (assign uuids)
			return null;
		}
		try {

			reportManagerPreRun(request_element);  // must run before prepareMetadata (assign uuids)


			if (parse_metadata) {
				metadata = new Metadata(request_element, parse_metadata, true);
			} else {
				metadata = MetadataParser.noParse(request_element);
			}

		}
		catch (MetadataValidationException e) {
			this.s_ctx.metadata_validation_error(e.getMessage());
			return metadata;
		}
		catch (MetadataException e) {
			e.printStackTrace();
			fatal("Error parsing metadata: filename is " + metadata_filename + ", Error is: " + e.getMessage());
		}

		addStandardLinkage(metadata);

		if (parse_metadata) {

			TestMgmt tm = new TestMgmt(testConfig);
			if ( assign_patient_id ) {
//				System.out.println("============================= assign_patient_id  in BasicTransaction#prepareMetadata()==============================");
				// getRetrievedDocumentsModel and insert PatientId
				String forced_patient_id = s_ctx.get("PatientId");
//                System.out.println("    to " + forced_patient_id)
//              s_ctx.dumpContextRecursive();
				if (s_ctx.useAltPatientId()) {
					forced_patient_id = s_ctx.get("AltPatientId");
				}
				HashMap<String, String> patient_map;
				patient_map = tm.assignPatientId(metadata, forced_patient_id);
				testLog.add_name_value(instruction_output, generate_xml("AssignedPatientId", patient_map));
			}

			// compile in results of previous steps
			if (use_id.size() > 0) {
				compileUseIdLinkage(metadata, use_id);
			}

			if ( assign_uids ) {
				Map<String, String> uniqueid_map = tm.assignUniqueIds(metadata, no_assign_uid_to);
				testLog.add_name_value(instruction_output, generate_xml("AssignedUids", uniqueid_map));
				if (reportManager == null)
					reportManager = new ReportManager(testConfig);
				reportManager.report(uniqueid_map, "_uid");
			}

			// assign sourceId
			Map<String, String> sourceid_map = tm.assignSourceId(metadata);
			testLog.add_name_value(instruction_output, generate_xml("AssignedSourceId", sourceid_map));

			// assign uuids
			if (assign_uuids) {
				IdParser ip = new IdParser(metadata);
				ip.compileSymbolicNamesIntoUuids();
				nameUuidMap = ip.getSymbolicNameUuidMap();
				testLog.add_name_value(instruction_output, generate_xml("AssignedUuids", nameUuidMap));
				if (reportManager == null)
					reportManager = new ReportManager(testConfig);
				reportManager.report(nameUuidMap, "_uuid");
			}

            // Insert test/section/step into authorPerson.id
            String stepId = getStep().getId();
            String testId = getStep().getPlan().getTestNum();
            String sectionId = getStep().getPlan().getCurrentSection();
            String id = String.format("%s/%s/%s", testId, sectionId, stepId);

//            if ("true".equals(Configuration.getProperty("testclient.addTestAsAuthor"))) {
//                if (metadata != null)
//                    metadata.addAuthorPersonToAll(id);
//            }

		}

		// from StoredQueryTransaction
		// compile in results of previous steps (linkage)
		if (use_id.size() > 0)
			compileUseIdLinkage(metadata, use_id);
		if (use_object_ref.size() > 0)
			compileUseObjectRefLinkage(metadata, use_object_ref);
		if (use_xpath.size() > 0)
			compileUseXPathLinkage(metadata, use_xpath);

		addStandardLinkage(metadata);

		return metadata;
	}

	//	public void compileExtraLinkage() throws XdsInternalException {
	//		Map<String, String> externalLinkage = s_ctx.getPlan().getExtraLinkage();
	//		if (externalLinkage != null) {
	//			Linkage linkage = new Linkage(testConfig);
	//			for (String key : externalLinkage.keySet()) {
	//				linkage.addLinkage(key, externalLinkage.getRetrievedDocumentsModel(key));
	//			}
	//			linkage.compileLinkage();
	//		}
	//	}

	public void compileExtraLinkage(OMElement root) throws XdsInternalException {
		Map<String, String> externalLinkage = s_ctx.getPlan().getExtraLinkage();
		if (externalLinkage != null) {
			Linkage linkage = new Linkage(testConfig);
			for (String key : externalLinkage.keySet()) {
				linkage.addLinkage(key, externalLinkage.get(key));
			}
			linkage.compileLinkage(root);

			if (reportManager == null)
				reportManager = new ReportManager(testConfig);
//			reportManager.report(externalLinkage);
		}
	}

	//	protected void logInputMetadata(OMElement metadata) throws XdsInternalException {
	//		s_ctx.add_name_value(instruction_output, "InputMetadata", Util.deep_copy(metadata));
	//	}

	protected void parse_assertion_instruction(OMElement assertion_part) throws XdsInternalException {
		Iterator elements = assertion_part.getChildElements();
		while (elements.hasNext()) {
			OMElement part = (OMElement) elements.next();
			String part_name = part.getLocalName();
			if (part_name.equals("DataRef")) {
				data_refs.add(part);
			}
			else if (part_name.equals("Assert")) {
				assertionEleList.add(part);
			}
		}
	}

    // report the parameters to the request as Reports so they can be referenced
    // in assertionEleList
    void reportStepParameters() {
        logger.info("generating linkageAsReports");
        if (reportManager == null)
            reportManager = new ReportManager(testConfig);
        Map<String, String> params = getStep().getPlan().getExtraLinkage();
        logger.info("transaction: " + params);
        for (String name : params.keySet()) {
            String value = params.get(name);
            ReportDTO reportDTO = new ReportDTO(name, value);
            logger.info("adding ReportDTO " + reportDTO);
            reportManager.addReport(reportDTO);
            logger.info("ReportBuilder manager has " + reportManager.toString());
        }
    }

	protected void parseBasicInstruction(OMElement part) throws XdsInternalException {
		String part_name = part.getLocalName();


//		if (part_name.equals("Metadata")) {
//			metadata_filename = "";
//			request_element = part.getFirstElement();
//		}
//		else
		if (part_name.equals("MetadataFile")) {
			metadata_filename = testConfig.testplanDir + File.separator + part.getText();
			testLog.add_name_value(this.instruction_output, "MetadataFile", metadata_filename);
		}
		else if (part_name.equals("AssignUuids")) {
			assign_uuids = true;
		}
		else if (part_name.equals("NoAssignUids")) {
			assign_uids = false;
			String id = part.getAttributeValue(MetadataSupport.id_qname);
			if (id != null && !id.equals("")) {
				no_assign_uid_to = id;
				assign_uids = true;
			}
		}
		else if (part_name.equals("NoConvert")) {
			this.no_convert = true;
		}
		else if (part_name.equals("Report")) {
			parseReportInstruction(part);
		}
		else if (part_name.equals("UseReport")) {
			parseUseReportInstruction(part);
		}
		else if (part_name.equals("ParseMetadata")) {
			String value = part.getText();
			testLog.add_name_value(this.instruction_output, "ParseMetadata", value);
			if (value.equals("False"))
				parse_metadata = false;
		}
		else if (part_name.equals("NoMetadata")) {
			noMetadataProcessing = true;
		}
		else if (part_name.equals("SOAPHeader")) {


			if (additionalHeaders == null)
				additionalHeaders = new ArrayList<OMElement>();
			testLog.add_name_value(this.instruction_output, "SOAPHeader", part.getFirstElement());

			additionalHeaders.add(part.getFirstElement());
		}
		else if (part_name.equals("WSSECHeader")) {

			//vbeera: commented below code
			/*
			 if(transactionSettings.issaml){
				if (wsSecHeaders == null)
					wsSecHeaders = new ArrayList<OMElement>();
				OMElement omElement = WSSESecurityHeaderUtil.getWSSecOMElement();
				System.out.println(omElement.getText());
				System.out.println("***************");
				System.out.println(omElement.toString());
				//this.s_ctx.add_name_value(omElement, "WSSECHeader", part.getFirstElement());
				this.s_ctx.add_name_value(this.instruction_output, "WSSECHeader", omElement);

				//wsSecHeaders.add(part.getFirstElement());
				wsSecHeaders.add(omElement);
			 }
			 */
		}
		else if (part_name.equals("UseId")) {
			use_id.add(part);
			testLog.add_name_value(instruction_output, "UseId", part);
		}
		else if (part_name.equals("UseRepositoryUniqueId")) {
			use_repository_unique_id.add(part);
			testLog.add_name_value(instruction_output, "UseRepositoryUniqueId", part);
		}
		else if (part_name.equals("Assertions")) {
			parse_assertion_instruction(part);
		}
		else if (part_name.equals("XDSb")) {
			xds_version = BasicTransaction.xds_b;
			testLog.add_simple_element(this.instruction_output, "Xdsb");
		}
		else if (part_name.equals("XDSa")) {
			xds_version = BasicTransaction.xds_a;
			testLog.add_simple_element(this.instruction_output, "Xdsa");
		}
		else if (part_name.equals("NoPatientId")) {
			assign_patient_id = false;
			testLog.add_simple_element(this.instruction_output, "NoPatientId");
		}
		else if (part_name.equals("SOAP11")) {
			soap_1_2 = false;
			testLog.add_simple_element(this.instruction_output, "SOAP11");
		}
		else if (part_name.equals("ASync")) {
			async = true;
			testLog.add_simple_element(this.instruction_output, "ASync");
		}
		else if (part_name.equals("WaitBefore")) {
			String millisecondsStr = part.getText();
			int milliseconds = 0;;
			try {
				milliseconds = Integer.parseInt(millisecondsStr);
			} catch (Exception e) {
				fatal("WaitBefore: cannot parse delay: " + millisecondsStr);
			}
			if (milliseconds == 0)
				fatal("WaitBefore: zero delay requested");
			try {
				long t0, t1, diff;
				System.out.print("Waiting " + milliseconds + " milliseconds ...");
				t0 = System.currentTimeMillis();
				do {
					t1 = System.currentTimeMillis();
					diff = t1 - t0;
				} while (diff < milliseconds);
				System.out.println("Done");
			} catch (Exception e) {
				fatal("WaitBefore failed: " + e.getMessage());
			}
		} else if (part_name.equals("InteractionSequence")) {
				// Nothing to parse here at the moment.
		} else {
			throw new XdsInternalException("BasicTransaction: Don't understand instruction " + part_name);
		}

//		if (testConfig.verbose)
//			System.out.println("<<<PRE-TRANSACTION>>>\n" + toString() + "<<<///PRE-TRANSACTION>>>\n");

	}

	protected void parseUseReportInstruction(OMElement part) throws XdsInternalException {
		if (useReportManager == null)
            useReportManager = new UseReportManager(testConfig);
		useReportManager.add(part);
	}

	protected void parseReportInstruction(OMElement part) {
		if (reportManager == null)
            reportManager = new ReportManager(testConfig);
		reportManager.addReport(part);
	}

	public class DocDetails {
		String uri;
		String size;
		String hash;
		String mimeType;
	};

	// called from RetrieveTransaction#retrieve_a only
	protected DocDetails getDocDetailsFromLogfile(OMElement uri_ref)
	throws FactoryConfigurationError, XdsInternalException, XdsInternalException,
	MetadataException, MetadataValidationException {
		DocDetails dd = new DocDetails();
		String log_file = uri_ref.getAttributeValue(new QName("log_file"));
		String step_id = uri_ref.getAttributeValue(new QName("step_id"));
		String transaction_type = uri_ref.getAttributeValue(new QName("trans_type"));

		Linkage l = new Linkage(testConfig);
		OMElement log = Util.parse_xml(testConfig.logFile);
		OMElement res = null;
		try {
			res = l.find_instruction_output(log, step_id, transaction_type);
		}
		catch (Exception e) {
			fail ("find_instruction_output failed: " + RegistryUtility.exception_details(e));
			throw new XdsInternalException(e.getMessage());
		}
		if (res == null) {
			fail ("Cannot find referenced instruction output");
			throw new XdsInternalException("Cannot find referenced instruction output");
		}

		OMElement result = XmlUtil.firstChildWithLocalName(res, "Result");
		if (result == null) {
			fail("Cannot find <Result/>");
			throw new XdsInternalException("Cannot find <Result/>");
		}

		Metadata m = MetadataParser.parseNonSubmission(result.getFirstElement());
		if (m.getExtrinsicObjectIds().size() == 0) {
			fail("No ExtrinsicObjects found in log file " + log_file + " step " + step_id +
					" with transaction type " + transaction_type);
			throw new XdsInternalException("No ExtrinsicObjects found in log file " + log_file + " step " + step_id +
					" with transaction type " + transaction_type);
		}
		OMElement eo = m.getExtrinsicObject(0);
		//dd.uri = m.getSlotValue(eo, "URI", 0);
		dd.uri = m.getURIAttribute(eo);
		dd.size = m.getSlotValue(eo, "size", 0);
		dd.hash = m.getSlotValue(eo, "hash", 0);
		dd.mimeType = m.getMimeType(eo);
		return dd;
	}

	public void runAssertionEngine(OMElement step_output, ErrorReportingInterface eri, OMElement assertion_output) throws XdsInternalException {

      AssertionEngine engine = new AssertionEngine();
      engine.setDataRefs(data_refs);
      engine.setCaller(this);

        try {
            if (useReportManager != null) {
                useReportManager.apply(assertionEleList);
            }
        } catch (Exception e) {
            failed();
        }

		engine.setAssertions(assertionEleList);
		engine.setLinkage(linkage);
		engine.setOutput(step_output);
		engine.setTestConfig(testConfig);
		engine.run(eri, assertion_output);
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpointService() throws XdsInternalException {
		if (endpoint == null || endpoint.equals(""))
			throw new XdsInternalException("Endpoint not initialized");
		int doubleSlash = endpoint.indexOf("//");
		if (doubleSlash == -1)
			throw new XdsInternalException("Endpoint contains no double slash");
		int singleSlash = endpoint.indexOf("/", doubleSlash+2);
		if (singleSlash == -1)
			throw new XdsInternalException("Endpoint has no service");
		return endpoint.substring(singleSlash);
	}

	public String getEndpointMachine() throws XdsInternalException {
		if (endpoint == null || endpoint.equals(""))
			throw new XdsInternalException("Endpoint not initialized");
		int doubleSlash = endpoint.indexOf("//");
		if (doubleSlash == -1)
			throw new XdsInternalException("Endpoint contains no double slash");
		int singleSlash = endpoint.indexOf("/", doubleSlash+2);
		if (singleSlash == -1)
			throw new XdsInternalException("Endpoint has no service");

		String machAndPort = endpoint.substring(doubleSlash+2, singleSlash);
		int colon = machAndPort.indexOf(":");
		if (colon == -1)
			return machAndPort;
		return machAndPort.substring(0, colon);
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getEndpointPort() throws XdsInternalException {
		if (endpoint == null || endpoint.equals(""))
			throw new XdsInternalException("Endpoint not initialized");
		int doubleSlash = endpoint.indexOf("//");
		if (doubleSlash == -1)
			throw new XdsInternalException("Endpoint contains no double slash");
		int singleSlash = endpoint.indexOf("/", doubleSlash+2);
		if (singleSlash == -1)
			throw new XdsInternalException("Endpoint has no service");

		String machAndPort = endpoint.substring(doubleSlash+2, singleSlash);
		int colon = machAndPort.indexOf(":");
		if (colon == -1)
			return "80";
		return machAndPort.substring(colon+1);
	}

	protected void validate_xds_version() throws XdsInternalException {
		if (xds_version == BasicTransaction.xds_none)
			throw new XdsInternalException("<XDSa/> or <XDSb/> must be specified in testplan.xml");
	}

	String getResponseAction() {
		return SoapActionFactory.getResponseAction(getRequestAction());
	}

	public OMElement getSecurityEl(String assertionStr) throws XdsInternalException  {
		String wsse = "<wsse:Security soapenv:mustUnderstand=\"true\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
		+ assertionStr
		+ "</wsse:Security>";

		return Util.parse_xml(wsse);
	}

	protected void soapCall(OMElement requestBody) throws Exception {
		soap = new Soap();
		testConfig.soap = soap;

		boolean samlEnabled = Installation.instance().propertyServiceManager().getPropertyManager().isEnableSaml();
		if(samlEnabled && transactionSettings.siteSpec != null && transactionSettings.siteSpec.isSaml){
			logger.info("SAML is ON. Preparing SAML header...");
			testConfig.saml = transactionSettings.siteSpec.isSaml;
			testConfig.gazelleXuaUsername = transactionSettings.siteSpec.getGazelleXuaUsername();
			soap.setGazelleXuaUsername(testConfig.gazelleXuaUsername);

			if (transactionSettings.siteSpec.getStsAssertion()!=null) {
				if (additionalHeaders==null)
					additionalHeaders = new ArrayList<OMElement>();
				additionalHeaders.add(getSecurityEl(transactionSettings.siteSpec.getStsAssertion()));
			}
		}
//		soap = testConfig.soap;
		soap.setAsync(async);
		soap.setUseSaml(testConfig.saml);

		/*
		if (testConfig.saml) {
			System.out.println("\tAxis2 client Repository: " + testConfig.testmgmt_dir + File.separator + "rampart" + File.separator + "client_repositories");
			System.out.println("\tEnabling WSSEC ...");
			soap.setRepositoryLocation(testConfig.testmgmt_dir + File.separator + "rampart" + File.separator + "client_repositories" );
		}
		*/

		if (additionalHeaders != null) {
			for (OMElement hdr : additionalHeaders)
//				try {
//					soap.addHeader(Util.deep_copy(hdr));
					soap.addHeader(hdr);
//				} catch (XdsInternalException e) {
//					s_ctx.set_error(e.getMessage());
//					failed();
//					logSoapRequest(soap);
//				}
		}

		/*
		if (wsSecHeaders != null) {
			for (OMElement hdr : wsSecHeaders)
				try {
					soap.addSecHeader(Util.deep_copy(hdr));
				} catch (XdsInternalException e) {
					s_ctx.set_error(e.getMessage());
					failed();
					logSoapRequest(soap);
				}
		}
		*/



		try {
			testLog.add_name_value(instruction_output, "InputMetadata", requestBody);

			soap.setSecurityParams(s_ctx.getTransactionSettings().securityParams);

			logger.info("Making soap call");
			soap.soapCall(requestBody,
					endpoint,
					useMtom, //mtom
					useAddressing,  // WS-Addressing
					soap_1_2,  // SOAP 1.2
					getRequestAction(),
					getResponseAction(), this.planContext.getExtraLinkage()
			);
			logger.info("back from making soap call");
		}
		catch (AxisFault e) {
			logger.info("soap fault");
			logSoapRequest(soap);
			logger.info("soap fault reported 1");
			s_ctx.set_error("SOAPFault: " + e.getMessage() + "\nEndpoint is " + endpoint);
			logger.info("soap fault reported 2");
			try {
				if (!s_ctx.expectFault())
					s_ctx.set_fault(e);
			} catch (Exception e1) { // throws fault - deal with it
			}
			logger.info("soap fault reported 3");
		}
		catch (XdsInternalException e) {
			logger.info("internal exception");
			s_ctx.set_error(e.getMessage());
			failed();
			logSoapRequest(soap);
		}
		finally {
			logger.info("finally");
			soap.clearHeaders();
		}

		logSoapRequest(soap);

		scanResponseForErrors();
	}


	protected boolean scanResponseForErrors() throws XdsInternalException {
		if (s_ctx.getExpectedStatus().size()==1 && s_ctx.getExpectedStatus().get(0).isSuccess()) {
			RegistryResponseParser registry_response = new RegistryResponseParser(getSoapResult());
			List<String> errs = registry_response.get_regrep_error_msgs();
			if (errs.size() > 0) {
                System.out.println("Received errors in response");
                for (String err : errs)
				    s_ctx.set_error(err);
				failed();
				return false;
			}
		}
		return true;
	}

	boolean soapRequestLogged = false;

	public void logSoapRequest(Soap soap) {
		if (soapRequestLogged) return;
		soapRequestLogged = true;
		try {
			testLog.add_name_value(instruction_output, "OutHeader", soap.getOutHeader());
			testLog.add_name_value(instruction_output, "OutAction", getRequestAction());
			testLog.add_name_value(instruction_output, "ExpectedInAction", getResponseAction());
			testLog.add_name_value(instruction_output, "InHeader", soap.getInHeader());
			testLog.add_name_value(instruction_output, "Result", soap.getResult());
		} catch (Exception e) {
			System.out.println("Cannot log soap request");
			e.printStackTrace();
		}
	}

//	protected void soapSend() {
//		soap = testConfig.soap;
//
//		try {
//			soap.soapSend(request_element,
//					endpoint,
//					useMtom, //mtom
//					useAddressing,  // WS-Addressing
//					soap_1_2,  // SOAP 1.2
//					getRequestAction()
//			);
//		}
//		catch (AxisFault e) {
//			if ( !s_ctx.expectFault()) {
//				s_ctx.set_fault(e);
//			}
//		}
//		catch (XdsInternalException e) {
//			s_ctx.set_error(e.getMessage());
//			failed();
//		}
//
//		try {
//			s_ctx.add_name_value(instruction_output, "OutAction", getRequestAction());
//			s_ctx.add_name_value(instruction_output, "OutHeader", soap.getOutHeader());
//		} catch (Exception e) {
//			System.out.println("oops");
//			e.printStackTrace();
//		}
//
//	}

	protected OMElement getSoapResult() {
		if (soap == null) return null;
		return soap.getResult();
	}

	protected void setMetadata(OMElement metadata_ele) {
		this.request_element = metadata_ele;
	}
	protected String validate_assertions(OMElement result, int metadata_type, OMElement test_assertions)
	throws XdsInternalException, MetadataException,
	MetadataValidationException {

		Metadata m = MetadataParser.parseNonSubmission(result);

		Validator v = new Validator(test_assertions);
		v.setInstruction_output(instruction_output);
		v.setTestConfig(testConfig);
		v.run_test_assertions(m);

      return v.getErrors();
   }

   /**
    * This method is overriden in subclasses which implement custom assertion
    * processing routines, as indicated by the {@code <Assertion>} element
    * having a process attribute. This code would only be called if an
    * {@code <Assertion>} element had such a value erroneously, that is, the
    * subclass does not actually implement this method. It is placed here to
    * avoid having to instantiate it in those classes.
    * @param engine AssertionEngine instance
    * @param assertion Assert being processed
    * @param assertion_output log.xml output element for that assert
    * @throws XdsInternalException if this method is invoked.
    */
   public void processAssertion(AssertionEngine engine, Assertion assertion, OMElement assertion_output)
      throws XdsInternalException {
      throw new XdsInternalException("BasicTransaction#processAssertion: unknown process " + assertion.toString());
   }

	public boolean isNoReportManagerPreRun() {
		return noReportManagerPreRun;
	}

	public void setNoReportManagerPreRun(boolean noReportManagerPreRun) {
		this.noReportManagerPreRun = noReportManagerPreRun;
	}

	public boolean isNoMetadataProcessing() {
		return noMetadataProcessing;
	}

	public void setNoMetadataProcessing(boolean noMetadataProcessing) {
		this.noMetadataProcessing = noMetadataProcessing;
	}
}
