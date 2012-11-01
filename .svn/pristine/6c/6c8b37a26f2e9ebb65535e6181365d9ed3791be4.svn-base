package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.XdsException;
import junit.framework.TestCase;


public class BasicTransactionTest extends TestCase {

	public void testEndpoint() throws XdsException {

		BasicTransactionWrapper b = new BasicTransactionWrapper(new StepContext(null), null, null);

		b.setEndpoint("http://localhost:9080/tf5/services/registryb");

		assertTrue("Service is " + b.getEndpointService(), b.getEndpointService().equals("/tf5/services/registryb"));

		assertTrue("Machine is " + b.getEndpointMachine(), b.getEndpointMachine().equals("localhost"));

		assertTrue("Port is " + b.getEndpointPort(), b.getEndpointPort().equals("9080"));
	}

}
