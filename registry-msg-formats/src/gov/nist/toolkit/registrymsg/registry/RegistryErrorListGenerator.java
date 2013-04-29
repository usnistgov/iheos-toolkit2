package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingCompletionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.ErrorLogger;
import gov.nist.toolkit.registrysupport.logging.LogMessage;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XDSMissingDocumentException;
import gov.nist.toolkit.xdsexception.XDSRepositoryMetadataException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

public class RegistryErrorListGenerator implements ErrorLogger, ErrorRecorder{
	public final static short version_2 = 2;
	public final static short version_3 = 3;
	String errors_and_warnings = "";
	boolean has_errors = false;
	boolean has_warnings = false;
	OMElement rel = null;
	StringBuffer validations = null;
	short version;
	protected OMNamespace ebRSns;
	protected OMNamespace ebRIMns;
	protected OMNamespace ebQns;
	boolean format_for_html = false;
	private final static Logger logger = Logger.getLogger(RegistryErrorListGenerator.class);
	boolean verbose = true;
	boolean log;
	boolean isXCA = false;
	
	public void setIsXCA() { isXCA = true; }
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public String toString() {
		if (rel == null) return "Null";
		return getRegistryErrorList().toString();
	}
	
	public RegistryErrorListGenerator() throws XdsInternalException {
		init(version_3, false);
	}
	
	public RegistryErrorListGenerator(GwtErrorRecorder er) throws XdsInternalException {
		init(version_3, false);
		
		if (!er.hasErrors())
			return;
		
		for (ValidatorErrorItem vei : er.getValidatorErrorInfo()) {
			if (vei.completion == ReportingCompletionType.ERROR) {
				addError(vei.msg, vei.getCodeString(), vei.location);
			} 
		}

	}

	public RegistryErrorListGenerator(short version) throws XdsInternalException {
		init(version, true /* log */);
	}

	public RegistryErrorListGenerator(short version, boolean log) throws XdsInternalException {
		init(version, log);
	}

	public void format_for_html(boolean value) { this.format_for_html = value; }

	void init(short version, boolean log)  throws XdsInternalException {
		if (version != version_2 && version != version_3) {
			throw new XdsInternalException("Class gov.nist.registry.ws.Response created without valid version");
		}
		this.version = version;
		if (version == version_2) {
			ebRSns =  MetadataSupport.ebRSns2;  
			ebRIMns = MetadataSupport.ebRIMns2;
			ebQns = MetadataSupport.ebQns2;
		} else {
			ebRSns =  MetadataSupport.ebRSns3;
			ebRIMns = MetadataSupport.ebRIMns3;
			ebQns = MetadataSupport.ebQns3;
		}
		this.log = log;
		this.validations = new StringBuffer();
	}

	public boolean has_errors() {
		return has_errors;
	}

	public String getStatus() {
		if (has_errors())
			return "Failure";
		return "Success";
	}

	public short getVersion() {
		return version;
	}

	public OMElement getRegistryErrorList() {
		//System.out.println(this.validations.toString());
		return registryErrorList();
	}

	OMElement registryErrorList() {
		if (rel == null)
			rel = MetadataSupport.om_factory.createOMElement("RegistryErrorList", ebRSns);
//		if (isXCA)
//			setHomeAsLocation();
		return rel;
	}
	
	public void setLocationPrefix(String prefix) {
		OMElement ele = registryErrorList();
		for (OMElement e : MetadataSupport.decendentsWithLocalName(ele, "RegistryError")) {
			OMAttribute at = e.getAttribute(MetadataSupport.location_qname);
			if (at == null) {
				at = MetadataSupport.om_factory.createOMAttribute("location", null, "");
				e.addAttribute(at);
			}
			at.setAttributeValue(prefix + at.getAttributeValue());
		}
	}
	
	static final QName codeContextQName = new QName("codeContext");

	public String getErrorsAndWarnings() {
		//		if ( !format_for_html)
		//			return errors_and_warnings;

		StringBuffer buf = new StringBuffer();
		for (Iterator<OMElement> it=getRegistryErrorList().getChildElements(); it.hasNext(); ) {
			OMElement ele = it.next();
			if (format_for_html)
				buf.append("<p>" + ele.getAttributeValue(codeContextQName) + "</p>\n");
			else
				buf.append(ele.getAttributeValue(codeContextQName)).append("\n");
		}
		return buf.toString();
	}

	public void add_warning(String code, String msg, String location, LogMessage log_message) {
		errors_and_warnings += "Warning: " + msg + "\n";
		warning(msg);
		addWarning(msg, code, location);

		if (log) {
			try {
				log_message.addErrorParam("Warning", msg);
			} catch (Exception e) {
				// oh well - can't fix it from here
			}
		}
	}

	public void add_validation(String topic, String msg, String location) {
		validations.append(topic + ": " +
				((msg != null) ? msg + " " : "") +
				((location != null) ? " @" + location : "") +
				"\n"
		);
	}

	public void add_error(String code, String msg, String location, String resource, LogMessage log_message) {
		errors_and_warnings += "Error: " + code + " " + msg + " (" + resource + ")\n";
		error(msg);
		addError(msg, code, location);

		if (log) {
			try {
				if (log_message != null)
					log_message.addErrorParam("Error",  msg + "\n" + location);
			} catch (Exception e) {
				// oh well - can't fix it from here
			}
		}
	}

	public void add_warning(String code, String msg, String location, String resource, LogMessage log_message) {
		errors_and_warnings += "Warning: " + code + " " + msg + " (" + resource + ")\n";
		warning(msg);
		addWarning(msg, code, location);

		if (log) {
			try {
				if (log_message != null)
					log_message.addErrorParam("Warning",  msg + "\n" + location);
			} catch (Exception e) {
				// oh well - can't fix it from here
			}
		}
	}

	public void add_error(Code code, String msg, String location, String resource, LogMessage log_message) {
		add_error(code.toString(), msg, location, resource, log_message);
	}

	public void add_error(String code, String msg, XdsException e, LogMessage log_message) {
		msg = msg + " (" + e.getResource() + ")";
		errors_and_warnings += "Error: " + code + " " + msg  + "\n";
		error(msg);
		addError(msg, code, e.getDetails());

		if (log) {
			try {
				if (log_message != null)
					log_message.addErrorParam("Error",  msg + "\n" + e.getDetails());
			} catch (Exception e1) {
				// oh well - can't fix it from here
			}
		}
	}

	HashMap<String, String> getErrorDetails(OMElement registryError) {
		HashMap<String, String>  err = new HashMap<String, String>();

		for (Iterator<OMAttribute> it=registryError.getAllAttributes(); it.hasNext(); ) {
			OMAttribute att = it.next();
			String name = att.getLocalName();
			String value = att.getAttributeValue();
			err.put(name, value);
		}

		return err;
	}
	
	public void addRegistryErrorsFromResponse(OMElement registryResponse) throws XdsInternalException {
		OMElement rel = MetadataSupport.firstChildWithLocalName(registryResponse, "RegistryErrorList");
		if (rel != null)
			addRegistryErrorList(rel, null);
	}

	public void addRegistryErrorList(OMElement rel, LogMessage log_message) throws XdsInternalException {
		addRegistryErrorList(rel, new ArrayList<String>(), log_message);
	}
	
	public void addRegistryErrorList(OMElement rel, List<String> errorCodesToFilter, LogMessage log_message) throws XdsInternalException {
		for (Iterator it=rel.getChildElements(); it.hasNext(); ) {
			OMElement registry_error = (OMElement) it.next();
			
			String code = registry_error.getAttributeValue(MetadataSupport.error_code_qname);
			if (errorCodesToFilter.contains(code))
				continue;

			if (log_message != null) {
				HashMap<String, String> err = getErrorDetails(registry_error);
				try {
					log_message.addErrorParam("Error", err.get("codeContext"));
				} catch (LoggerException e) {
					throw new XdsInternalException(ExceptionUtil.exception_details(e));
				}
			}


			OMElement registry_error_2 = Util.deep_copy(registry_error);

			logger.error("registry_error2 is \n" + registry_error_2.toString());

			if (this.getVersion() == RegistryErrorListGenerator.version_3)
				registry_error_2.setNamespace(MetadataSupport.ebRSns3);
			registryErrorList().addChild(registry_error_2);
			String severity = registry_error.getAttributeValue(MetadataSupport.severity_qname);
			severity = new Metadata().stripNamespace(severity);
			if (severity.equals("Error")) 
				has_errors = true;
			else
				has_warnings = true;
		}
	}


	public boolean hasContent() {
		return this.has_errors || this.has_warnings;
	}

	public void addError(String msg) {
		addError(msg, "", "");
	}

	public void addError(String context, String code, String location)  {
		if (context == null) context = "";
		if (code == null) code = "";
		if (location == null) location = "";
		OMElement error = MetadataSupport.om_factory.createOMElement("RegistryError", ebRSns);
		error.addAttribute("codeContext", context, null);
		error.addAttribute("errorCode", code, null);
		error.addAttribute("location", location, null);
		String severity;
		if (version == version_3) 
			severity = MetadataSupport.error_severity_type_namespace + "Error";
		else
			severity = "Error";
		error.addAttribute("severity", severity, null);
		registryErrorList().addChild(error);
		this.has_errors = true;
	}


	public void delError(String context) {
		OMElement errs = registryErrorList();

		for (Iterator<OMElement> it = errs.getChildElements(); it.hasNext(); ) {
			OMElement e = it.next();
			if (context != null) {
				String ctx = e.getAttributeValue(codeContextQName);
				if (ctx != null && ctx.indexOf(context) != -1)
					e.detach();
				continue;
			}
		}
	}

	public void addWarning(String context, String code, String location) {
		if (context == null) context = "";
		if (code == null) code = "";
		if (location == null) location = "";
		OMElement error = MetadataSupport.om_factory.createOMElement("RegistryError", ebRSns);
		error.addAttribute("codeContext", context, null);
		error.addAttribute("errorCode", code, null);
		error.addAttribute("location", location, null);
		error.addAttribute("severity", MetadataSupport.error_severity_type_namespace + "Warning", null);
		registryErrorList().addChild(error);
		this.has_warnings = true;
	}

	public void error(String msg) {
		if (verbose)
		System.out.println("ERROR: " + msg);
	}

	public void warning(String msg) {
		if (verbose)
		System.out.println("WARNING: " + msg);
	}

	public static String exception_details(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);

		return "Exception thrown: " + e.getClass().getName() + "\n" + e.getMessage() + "\n" + new String(baos.toByteArray());
	}

//	@SuppressWarnings("unchecked")
//	void setHomeAsLocation() {
//		String reXPath = "//*[local-name()='RegistryError']";
//		String home = Properties.loader().getString("home_community_id");;
//		try {
//			AXIOMXPath xpathExpression = new AXIOMXPath (reXPath);
//			List<?> nodes = xpathExpression.selectNodes(rel);
//			for (OMElement node : (List<OMElement>) nodes) {
//				node.addAttribute("location", home, null);
//			}
//		} catch (JaxenException e) {
//		}
//	}

	public void err(String msg, String resource) {
		addError(msg);
	}
	
	public void err(XDSMissingDocumentException e) {
		this.add_error("XDSMissingDocument", e.getMessage(), e, null);
	}
	
	public void err(XDSRepositoryMetadataException e) {
		this.add_error("XDSRepositoryMetadataError", e.getMessage(), e, null);
	}
	
	public void err(Exception e) {
		addError(ExceptionUtil.exception_details(e));
	}

	public void finish() {
	}

	public void sectionHeading(String msg) {
	}

	public void challenge(String msg) {
	}

	public void showErrorInfo() {
	}

	public void detail(String msg) {
		// TODO Auto-generated method stub
		
	}

	public void externalChallenge(String msg) {
		// TODO Auto-generated method stub
		
	}

	public void err(String code, String msg, String location, String resource,
			Object logMessage) {
		this.add_error(code, msg, location, resource, (LogMessage) logMessage);
	}

	public void err(Code code, String msg, String location, String resource,
			Object log_message) {
		add_error(code, msg, location, resource, (LogMessage) log_message);
		
	}

	public void err(Code code, String msg, String resource) {
		add_error(code, msg, "", resource, null);
		
	}

	public void err(Code code, Exception e) {
		add_error(code, ExceptionUtil.exception_details(e), null, null, null);

		
	}

	public void err(Code code, String msg, String location, String resource) {
		add_error(code, msg, location, resource, null);
	}

	public void err(Code code, String msg, Object location, String resource) {
		String loc = "";
		if (location != null)
			loc = location.getClass().getSimpleName();
		add_error(code, msg, loc, resource, null);
	}

	public boolean hasErrors() {
		return has_errors;
	}

	public void err(String code, String msg, String location, String severity,
			String resource) {
		add_error(code, msg, location + " - " + severity, resource, null);
	}

	public void err(Code code, String msg, String location, String severity,
			String resource) {
		if (severity != null && severity.equalsIgnoreCase("Warning"))
			add_warning(code.toString(), msg, location, resource, null);
		else
			this.add_error(code, msg, location, resource, null);
	}

	@Override
	public void warning(String code, String msg, String location,
			String resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warning(Code code, String msg, String location, String resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder() {
		return this;
	}

	@Override
	public int getNbErrors() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void concat(ErrorRecorder er) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ValidatorErrorItem> getErrMsgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorRecorderBuilder getErrorRecorderBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void success(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warning(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void summary(String msg, boolean success, boolean part) {
		// TODO Auto-generated method stub
		
	}

}
