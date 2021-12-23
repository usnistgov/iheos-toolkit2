package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import java.util.logging.Logger;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegistryResponseParser {
	OMElement response_element;
	RegistryResponse response;
	static Logger logger = Logger.getLogger(RegistryResponseParser.class.getName());

	public class RegistryResponse {
		boolean success;
		List<RegistryError> registryErrorList = new ArrayList<RegistryError>();
		OMElement topElement;

		public boolean isSuccess() { return success; }
		public List<RegistryError> getRegistryErrorList() { return registryErrorList; }
		public OMElement getTopElement() { return topElement; }

		public void addErrorsTo(RegistryErrorListGenerator rel) {
			for (RegistryError e : registryErrorList) {
				rel.add_error(e.errorCode, e.codeContext, e.location, null, null);
			}
		}
	}

	public RegistryResponseParser(OMElement response_element)  {
		this.response_element = response_element;
	}

	public RegistryResponse getRegistryResponse() throws XdsInternalException {
		parse();
		return response;
	}

	void parse() throws XdsInternalException {
		response = new RegistryResponse();
		response.success = is_error();
		response.topElement = response_element;

		OMElement registry_error_list = null;
		if (response_element.getLocalName().equals("RegistryErrorList"))
			registry_error_list = response_element;
		else
			registry_error_list = XmlUtil.firstChildWithLocalName(response_element, "RegistryErrorList") ;

		if (registry_error_list == null)
			return;

		response.registryErrorList = new RegistryErrorListParser(registry_error_list).getRegistryErrorList();

	}

	// also returns errorCode attributes
	public ArrayList<String> get_error_code_contexts() {
		ArrayList<String> result = new ArrayList<String>();

		OMElement registry_error_list = null;
		if (response_element.getLocalName().equals("RegistryErrorList"))
			registry_error_list = response_element;
		else
			registry_error_list = XmlUtil.firstChildWithLocalName(response_element, "RegistryErrorList") ;

		if (registry_error_list == null)
			return result;
		for (OMElement registry_error : XmlUtil.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String severity = get_att(registry_error, "severity");
			if (severity == null || !severity.endsWith("Error"))
				continue;
			String code_context = get_att(registry_error, "codeContext");
			if (code_context != null)
				result.add(code_context);

			String error_code = get_att(registry_error, "errorCode");
			if (error_code != null)
				result.add(error_code);
		}

		return result;
	}

	public ArrayList<String> get_error_codes() {
		ArrayList<String> result = new ArrayList<String>();

		OMElement registry_error_list = null;
		if (response_element.getLocalName().equals("RegistryErrorList"))
			registry_error_list = response_element;
		else
			registry_error_list = XmlUtil.firstChildWithLocalName(response_element, "RegistryErrorList") ;

		if (registry_error_list == null)
			return result;
		for (OMElement registry_error : XmlUtil.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String errorCode = get_att(registry_error, "errorCode");
			if (errorCode == null)
				continue;
			if (errorCode != null)
				result.add(errorCode);
		}

		return result;
	}

	public String get_regrep_error_msg() {
		List<String> msgs = get_regrep_error_msgs();
		StringBuilder buf = new StringBuilder();
		for (String x : msgs) buf.append(x);
		return buf.toString();
	}

	public List<String> get_regrep_error_msgs() {
		List<String> messages = new ArrayList<>();
		if (response_element == null)
			return asList("No Message");
		OMElement registry_response = XmlUtil.firstChildWithLocalName(response_element, "RegistryResponse") ;
		OMElement current = (registry_response == null) ? response_element : registry_response;
		OMElement registry_error_list = XmlUtil.firstChildWithLocalName(current, "RegistryErrorList") ;
		if (registry_error_list == null)
			return messages;
		for (OMElement registry_error : XmlUtil.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String severity = registry_error.getAttributeValue(MetadataSupport.severity_qname);
			if (severity != null && severity.endsWith("Warning"))
				continue;
			String msg =
					registry_error.getAttributeValue(new QName("errorCode")) + "  :  " +
							registry_error.getAttributeValue(new QName("codeContext")) + "  :  " +
							registry_error.getText();
			if (msg == null) continue;
			messages.add(msg);
		}
		return messages;
	}

	List<String> asList(String value) {
		List<String> lst = new ArrayList<>();
		lst.add(value);
		return lst;
	}

	public String get_registry_response_status() throws XdsInternalException {
		if ("Fault".equals(response_element.getLocalName()))
			return "Fault";
		try {
			AXIOMXPath xpathExpression = new AXIOMXPath ("@status");
			List nodeList = xpathExpression.selectNodes(response_element);
			Iterator it = nodeList.iterator();
			if (! it.hasNext()) {
				throw new XdsInternalException("RegistryResponse:get_registry_response_status: Cannot retrieve /RegistryResponse/@status");
			}
			OMAttribute att = (OMAttribute) it.next();
			return att.getAttributeValue();
		} catch (JaxenException e) {
			throw new XdsInternalException("Jaxen Exception from get_registry_response_status: " + e.getMessage());
		}
	}

	public boolean is_error() throws XdsInternalException {
		String status = get_registry_response_status();
		return  ! status.endsWith("Success");
	}

	String get_att(OMElement ele, String name) {
		OMAttribute att = ele.getAttribute(new QName(name));
		if (att == null)
			return null;
		return att.getAttributeValue();

	}

}
