package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.RegistryErrorLog;
import gov.nist.toolkit.registrysupport.logging.RegistryResponseLog;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestStepLogContent  implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2676088682465214583L;
    static Logger logger = Logger.getLogger(TestStepLogContent.class);
	String id;
	boolean success;
	boolean expectedSuccess;
	boolean expectedWarning;
	transient OMElement root;
	StepGoals goals;
	String endpoint;
	List<String> errors;
	List<String> details;
	List<String> reports;
	String inputMetadata;
	String result;
	String inHeader = null;
	String outHeader = null;
	String rootString;

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("[TestStepLog: ");

		buf.append(" id=" + id);
		buf.append(" success=" + success);
		buf.append(" endpoint=" + getEndpoint());

		buf.append("]");

		return buf.toString();
	}

	public TestStepLogContent(OMElement root) throws Exception {
		this.root = root;
		String stat = root.getAttributeValue(MetadataSupport.status_qname);

		// hack until NewPatientId instructions generates proper status
		if (stat == null)
			success = true;
		else
			success = "Pass".equals(stat);
		id = root.getAttributeValue(MetadataSupport.id_qname);

		OMElement expectedStatusEle = XmlUtil.firstChildWithLocalName(root, "ExpectedStatus");
		if (expectedStatusEle == null)
			expectedSuccess = true;
		else {
			String expStat = expectedStatusEle.getText();
			if ("Success".equals(expStat)) {
				expectedSuccess = true;
				expectedWarning = false;
			} else if ("Failure".equals(expStat)) {
				expectedSuccess = false;
				expectedWarning = false;
			} else if ("Warning".equals(expStat)) {
				expectedWarning = true;
				expectedSuccess = false;
			} else
				throw new Exception("TestStep: Error parsing log.xml file: illegal value (" + expStat + ") for ExpectedStatus element of step " + id);
		}
		parseGoals();
		parseEndpoint();
        parseDetails();
		parseErrors();
		parseInHeader();
		parseOutHeader();
		parseResult();
		parseInputMetadata();
		parseRoot();
		parseReports();
	}

	public StepGoals getGoals() {
		return goals;
	}

	void parseGoals() {
		goals = new StepGoals(id);

		for (OMElement ele : XmlUtil.childrenWithLocalName(root, "Goal")) {
			goals.goals.add(ele.getText());
		}

	}

	public boolean getStatus() {
		return success;
	}

	public String getName() {
		return id;
	}

	/**
	 * Get response message for this test step.
	 * @return OMElement of message
	 * @throws Exception if no result
	 */
	@SuppressWarnings("unchecked")
	public OMElement getRawResult() throws Exception {
		for (Iterator<OMElement> it=root.getChildElements(); it.hasNext(); ) {
			OMElement ele1 = it.next();
			for (Iterator<OMElement> it2=ele1.getChildElements(); it2.hasNext(); ) {
				OMElement ele2 = it2.next();
				if ("Result".equals(ele2.getLocalName())) {
					return ele2.getFirstElement();
				}
			}
		}
		throw new Exception("Step: " + id + " has no &ltResult/> block");
	}

	public RegistryResponseLog getRegistryResponse() throws Exception {
		return new RegistryResponseLog(getRawResult());
	}

	public Metadata getMetadata() throws Exception {
		return MetadataParser.parseNonSubmission(getRawResult());
	}

	public RegistryResponseLog getUnexpectedErrors() throws Exception {
		return new RegistryResponseLog(getRegistryResponse().getErrorsDontMatch(getExpectedErrorMessage()));
	}

	public String getExpectedErrorMessage() {
		OMElement expEle = root.getFirstChildWithName(MetadataSupport.expected_error_message_qname);
		if (expEle == null)
			return null;
		return expEle.getText();
	}

	public String getEndpoint() {
		return endpoint;
	}

	void parseEndpoint() {
		List<OMElement> endpoints = XmlUtil.decendentsWithLocalName(root, "Endpoint");
		if (endpoints.isEmpty())
			return;
		endpoint = endpoints.get(0).getText();
	}

	public List<String> getAssertionErrors() {
		List<OMElement> errorEles = XmlUtil.decendentsWithLocalName(root, "Error");
		List<String> errors = new ArrayList<String>();

		for (OMElement errorEle : errorEles ){
			errors.add(errorEle.getText());
		}

		return errors;
	}

	public List<String> getErrors() throws Exception {
		return errors;
	}

	void parseErrors() throws Exception {
		errors = new ArrayList<>();

		try {
			RegistryResponseLog rrl = getUnexpectedErrors();
			for (int i=0; i<rrl.size(); i++) {
				RegistryErrorLog rel = rrl.getError(i);
				errors.add(rel.getSummary());
			}
		} catch (Exception e) {}
		errors.addAll(getAssertionErrors());
//        if (errors.size() > 0) success = false;
	}


	public List<String> getSoapFaults() {
		List<String> errs = new ArrayList<String>();

		for (OMElement errEle : XmlUtil.childrenWithLocalName(root, "SOAPFault")) {
			String err = errEle.getText();
			errs.add(id + ": " + err);
		}

		return errs;
	}

	private void parseDetails() {
		details = new ArrayList<String>();

		//for (OMElement ele : MetadataSupport.childrenWithLocalName(root, "Detail")) {
		for (OMElement ele : XmlUtil.decendentsWithLocalName(root, "Detail")) {
			String detail = ele.getText();
			details.add(detail);
		}
	}

	private static final QName nameQname = new QName("name");

	private void parseReports() {
		reports = new ArrayList<String>();

		for (OMElement ele : XmlUtil.decendentsWithLocalName(root, "Report")) {
			String name = ele.getAttributeValue(nameQname);
			String value = ele.getText();
			reports.add(name + " = " + value);
		}

	}

	public List<String> getReports() {
		return reports;
	}

	public List<String> getDetails() {
		return details;
	}

	public String getInputMetadata() {
		return inputMetadata;
	}

	void parseInputMetadata() {
		try {
			inputMetadata = xmlFormat( getRawInputMetadata() );
		} catch (Exception e) {
		}
	}

	public OMElement getRawInputMetadata() {
		return XmlUtil.firstDecendentWithLocalName(root, "InputMetadata").getFirstElement();
	}

	public Metadata getParsedInputMetadata() throws MetadataValidationException, MetadataException {
		return MetadataParser.parseNonSubmission(getRawInputMetadata());
	}

	public OMElement getRawReports() {
		try {
			return XmlUtil.firstDecendentWithLocalName(root, "Reports");
		} catch (Exception e) {
			return null;
		}
	}

	public String getResult() {
		return result;
	}

	void parseResult() {
		try {
			OMElement copy = Util.deep_copy(XmlUtil.firstDecendentWithLocalName(root, "Result").getFirstElement());
			for (OMElement ele : XmlUtil.decendentsWithLocalName(copy, "Document", 4)) {
				String original = ele.getText();
				int size = (original == null || original.equals("")) ? 0 : original.length();
				ele.setText("Base64 contents removed by XDS Toolkit prior to display (" + size + " characters)");
			}
			result =  xmlFormat(copy);
		} catch (Exception e) {
		}
	}

	public String getInHeader() {
		return inHeader;
	}

	void parseInHeader() {
		try {
			inHeader = xmlFormat(XmlUtil.firstDecendentWithLocalName(root, "InHeader").getFirstElement());
		} catch (Exception e) {
		}
	}

	public String getOutHeader() {
		return outHeader;
	}

	void parseOutHeader() {
		try {
			outHeader = xmlFormat(XmlUtil.firstDecendentWithLocalName(root, "OutHeader").getFirstElement());
		} catch (Exception e) {
		}
	}

	public String getRoot() {
		return rootString;
	}

	void parseRoot() {
		try {
			rootString = xmlFormat(root);
		} catch (Exception e) {}
		//		rootString = root.toString();
	}

	String xmlFormat(OMElement ele) throws XdsInternalException, FactoryConfigurationError {
		if (ele == null)
			return "";
		try {
			return new OMFormatter(ele.toString()).toHtml();
		} catch (Exception e) {
			return ExceptionUtil.exception_details(e);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}


}
