package gov.nist.toolkit.soap.axis2;

import junit.framework.TestCase;

public class SwaTest extends TestCase {
	String endpoint1 = "http://localhost:50/foo/bar/axil?r=4&s=3";
	Swa swa;
	
	public void setUp() {
		swa = new Swa();
		swa.endpoint = endpoint1;
	}
	
	public void test_parse() throws Exception {
		swa.parse_endpoint();
		assertTrue(swa.protocol.equals("http"));
		assertTrue(swa.host.equals("localhost"));
		assertTrue(swa.port.equals("50"));
		assertTrue(swa.service.equals("/foo/bar/axil?r=4&s=3"));
	}

}
