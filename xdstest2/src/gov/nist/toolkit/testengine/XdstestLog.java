package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

public class XdstestLog {
	Map<String, OMElement> steps;  // id => TestStep
	boolean status;

	public XdstestLog(File logfile) throws XdsInternalException, FactoryConfigurationError {
				
		OMElement log = Util.parse_xml(logfile);
		
		List<OMElement> stepLst = MetadataSupport.childrenWithLocalName(log, "TestStep");
		for (OMElement stp : stepLst) {
			String id = stp.getAttributeValue(MetadataSupport.id_qname);
			steps.put(id, stp);
		}
		status = "Pass".equals((log.getAttributeValue(new QName("status"))));
	}
	
	public XdsTestStepLog getStepLog(String stepName) throws Exception {
		OMElement step = steps.get(stepName);
		if (step == null)
			throw new Exception("Step " + stepName + " does not exist");
		return new XdsTestStepLog(step);
	}
	
	public List<String> getStepNames() {
		List<String> names = new ArrayList<String>();
		for (String name : steps.keySet()) {
			names.add(name);
		}
		return names;
	}
	
}
