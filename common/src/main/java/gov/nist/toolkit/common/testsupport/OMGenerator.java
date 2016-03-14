package gov.nist.toolkit.common.testsupport;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;

public class OMGenerator {

	OMFactory om_factory = null;

	public OMGenerator() {
		super();
	}

	protected OMFactory om_factory() {
		if (om_factory == null)
			om_factory = OMAbstractFactory.getOMFactory();
		return om_factory;
	}

}