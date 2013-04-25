package gov.nist.toolkit.dns.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nist.toolkit.dns.DnsLookup;

import org.junit.Test;
import org.xbill.DNS.TextParseException;

public class DnsLookupTest {

	
	public void testMX() {
		try {
			assertEquals( new DnsLookup().getMxRecord("ttt.transparenthealth.org"), "23.21.244.250");
		} catch (TextParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCERT() {
		DnsLookup dl = new DnsLookup();
		try {
			String cert = dl.getCertRecord("glacecentral.com");
			assertNotNull(cert);
		} catch (TextParseException e) {
			e.printStackTrace();
			fail();
		}
	}

}
