package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.NotALogFileException;
import gov.nist.toolkit.testenginelogging.SectionLogMap;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlanContext extends BasicContext {
	OMElement results_document = null;
	String defaultRegistryEndpoint = null;
	Map<String, String> externalLinkage = null;
	Map<String, Object> externalLinkage2 = null;  // for binary stuff like certificates
	SectionLogMap previousSectionLogs;
	LogFileContent currentSectionLog;
	String currentSection;
	TestConfig testConfig;
	TransactionSettings transactionSettings = null;

	public void setTransactionSettings(TransactionSettings ts) {
		this.transactionSettings = ts;
		if (ts.patientId != null)
			setPatientId(ts.patientId);
	}
	
	public void setTestConfig(TestConfig config) {
		testConfig = config;
	}
	
	public void setCurrentSection(String sectionName) {
		currentSection = sectionName;
	}
	
	public void setPreviousSectionLogs(SectionLogMap previousLogs) {
		previousSectionLogs = previousLogs;
        logger.debug(previousLogs.describe());
	}
	
	public SectionLogMap getPreviousSectionLogs() {
		if (previousSectionLogs == null)
			previousSectionLogs = new SectionLogMap();
//		else
//			System.out.println("\tHave logs for " + previousSectionLogs.sectionNames);

		return previousSectionLogs;
	}
	
	public void setExtraLinkage(Map<String, String> linkage) {
        StringBuilder buf = new StringBuilder();
        buf.append("ExtraLinkage...\n");
        for (String key : linkage.keySet())
            buf.append("...").append(key).append(" ==> ").append(linkage.get(key)).append("\n");
        logger.debug(buf.toString());
		externalLinkage = linkage;
	}
	
	public void setExtraLinkage2(Map<String, Object> linkage) {

        externalLinkage2 = linkage;
	}
	
	public Map<String, String> getExtraLinkage() {
		return externalLinkage;
	}
	
	public Map<String, Object> getExtraLinkage2() {
		return externalLinkage2;
	}
	
	public void setCurrentSectionLog(OMElement ele) throws NotALogFileException, Exception {
		currentSectionLog = new LogFileContent(ele);
	}
	
	public LogFileContent getCurrentSectionLog() {
		return currentSectionLog;
	}
	
	public String getDefaultRegistryEndpoint() {
		return defaultRegistryEndpoint;
	}

	public void setDefaultRegistryEndpoint(String defaultRegistryEndpoint) {
		this.defaultRegistryEndpoint = defaultRegistryEndpoint;
	}

	String default_patient_id = null;
	String registry_endpoint = null;
	String patient_id = null;
	String alt_patient_id = null;
	boolean status = true;
	String test_num = "0";
	short xds_version = BasicTransaction.xds_none;
	ArrayList<OMElement> phone_home_log_files = null;
	private final static Logger logger = Logger.getLogger(PlanContext.class);
	boolean writeLogFiles = true;

	public void setWriteLogFiles(boolean write) {
		writeLogFiles = write;
	}

	public List<OMElement> getPhoneHomeLogs() {
		return phone_home_log_files;
	}

	public String getTestNum() {
		return test_num;
	}

	void setRegistryEndpoint(String end_point) {
		registry_endpoint = end_point;
		logger.info("Set Registry Endpoint = " + end_point);
		set("RegistryEndpoint", end_point);
	}

	public String getRegistryEndpoint() {
		return registry_endpoint;
	}

	public void setPatientId(String patient_id) {
		this.patient_id = patient_id;
		set("PatientId", patient_id);
	}

	public void setAltPatientId(String patient_id) {
		this.alt_patient_id = patient_id;
		set("AltPatientId", patient_id);
	}

	public OMElement getResultsDocument() {
		return results_document;
	}

	public PlanContext() {
		super(null);
	}

//	public PlanContext(short xds_version) {
//		super(null);
//		this.xds_version = xds_version;
//	}

	void set_status_in_output() {
		results_document.addAttribute("status", (status) ? "Pass" : "Fail", null);	
	}

	boolean getStatus() {
		return status;
	}

	void setDefaultConfig(OMElement config) {
		Iterator<OMElement> elements = config.getChildElements();
		while (elements.hasNext()) {
			OMElement part = elements.next();
			String part_name = part.getLocalName();

			String value = part.getText();

			set(part_name, value);
		}
	}

	boolean run(File testplanFile)  throws Exception {
		if (testConfig.verbose)
			System.out.println("Run section " + testplanFile);
		try {
			results_document = build_results_document();	

			testLog.add_name_value(results_document, "Xdstest2_version", testConfig.testkitVersion);
			testLog.add_name_value(results_document, "Xdstest2_args", testConfig.args);
			testLog.add_name_value(results_document, "testkit_version", testConfig.testkitVersion);

			//			String testplan_str = Io.stringFromFile(testplanFile);
			//			
			//			StringSub str_sub = new StringSub();
			//			str_sub.setString(testplan_str);
			//			testplan_str = str_sub.toString();

			//			OMElement testplan = Util.parse_xml(new File(testplan_filename));
			OMElement testplan = Util.parse_xml(testplanFile);
			
			if (externalLinkage != null) {
				Linkage l = new Linkage(testConfig);
				l.addLinkage(externalLinkage);
				l.apply(testplan);
			}

			Iterator elements = testplan.getChildElements();
			while (elements.hasNext()) {
				OMElement part = (OMElement) elements.next();
				String part_name = part.getLocalName();

				// as we step through the plan, all plan elements are copied to results file with
				// add_name_value()
				if (part_name.equals("RegistryEndpoint")) 
				{
					defaultRegistryEndpoint = part.getText();
					testLog.add_name_value(results_document, part); 
					setRegistryEndpoint(defaultRegistryEndpoint);
				} 
				else if (part_name.equals("PatientId")) 
				{
					default_patient_id = part.getText();
					testLog.add_name_value(results_document, part);
					setPatientId(default_patient_id);
				} 
				else if (part_name.equals("Test")) 
				{
					testLog.add_name_value(results_document, part);
					test_num = part.getText();
				} 
				else if (part_name.equals("Rule")) 
				{
				} 
				else if (part_name.equals("TestStep")) 
				{
					StepContext step_context = new StepContext(this);
					step_context.setTestConfig(testConfig);
					step_context.setTransationSettings(transactionSettings);

					step_context.run(part, this);

					if ( !step_context.getStatus() )
						status = false;
				} 
				else 
				{
					throw new XdsInternalException("Don't understand test step named " + part_name);
				}
				
				if (status == false)
					break;
			}

			if (test_num.equals("0") )
				throw new XdsInternalException("<Test/> missing from testplan");

			set_status_in_output();


		} 
		catch (MetadataValidationException e) {
			testLog.add_name_value(results_document, "MetadataValidationError", ExceptionUtil.exception_details(e));
			status = false;
			set_status_in_output();
			transactionSettings.res.add(e.getMessage(), "", false);
		}
		catch (XdsException e) {  
			String errorDetails = ExceptionUtil.exception_details(e);
			testLog.add_name_value(results_document, "FatalError", errorDetails);
            logger.error(errorDetails);
			status = false;
			set_status_in_output();
			transactionSettings.res.add(e.getMessage(), "", false);
		}
		catch (Exception e) {  
			testLog.add_name_value(results_document, "FatalError", ExceptionUtil.exception_details(e));
			status = false;
			set_status_in_output();
			transactionSettings.res.add(e.getMessage(), "", false);
			throw e;  // error handler above reports error in UI
		}

		if (writeLogFiles) {
			File logFile = null;
			try {
				logFile = testConfig.logFile;
				FileOutputStream os = new FileOutputStream(logFile);
				//System.out.println(results_document.toString());
				//String results_string = results_document.toString();
				results_document.build();
				String results_string = new OMFormatter(results_document).toString();
				os.write(results_string.getBytes());
				os.flush();
				os.close();

//				if (phone_home_log_files == null) {
//					phone_home_log_files = new ArrayList<OMElement>();
//				}
//				phone_home_log_files.add(results_document);

			} catch (FileNotFoundException e) {
				System.out.println("Cannot create file log.xml (" + logFile + ")");
				System.exit(-1);
			} catch (IOException e) {
				System.out.println("Cannot write to file log.xml (" + logFile + ")");
				System.exit(-1);
			}
		}

		return getStatus();

	}
	
	public OMElement getLog() {
		return results_document;
	}

}
