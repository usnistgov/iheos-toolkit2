package gov.nist.toolkit.registrymsgold.registry;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class RegistryErrorList {
	List<RegistryError> errors = new ArrayList<RegistryError>();

	public RegistryErrorList(OMElement xml) {
		if (xml == null || !"RegistryErrorList".equals(xml.getLocalName()))
			return;

		for (OMElement re : XmlUtil.childrenWithLocalName(xml, "RegistryError")) {
			errors.add(new RegistryError(re));
		}
	}

	public boolean hasError() {
		for (RegistryError e : errors) {
			if (e.isError())
				return true;
		}
		return false;
	}

	public void validate(ErrorRecorder er, ValidationContext vc) {
		for (RegistryError re : errors) {
			re.validate(er, vc);
		}
	}
}
