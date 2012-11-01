package gov.nist.toolkit.valccda;

import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

// does the supplied chunk of XML look like a CDA document?
public class CdaDetector {
	
	public CdaDetector() {}
	
	public boolean isCDA(OMElement ele) {
		if (ele == null)
			return false;
		if (ele.getLocalName().equals("ClinicalDocument"))
			return true;
		return false;
	}
	
	public boolean isCDA(String xml_text) throws XdsInternalException, FactoryConfigurationError {
		OMElement ele = Util.parse_xml(xml_text);
		return isCDA(ele);
	}
	
	public boolean isCDA(InputStream is) throws XdsInternalException, FactoryConfigurationError {
		OMElement ele = Util.parse_xml(is);
		return isCDA(ele);
	}

	public boolean isCDA(byte[] in) throws XdsInternalException, FactoryConfigurationError {
		OMElement ele = Util.parse_xml(new String(in));
		return isCDA(ele);
	}

}
