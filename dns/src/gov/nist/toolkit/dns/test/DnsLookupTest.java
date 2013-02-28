package gov.nist.toolkit.dns.test;

import gov.nist.toolkit.dns.DnsLookup;

import org.junit.Test;
import org.xbill.DNS.TextParseException;

import static org.junit.Assert.*;

public class DnsLookupTest {

	@Test
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
		try {
			assertEquals( new DnsLookup().getCertRecord("ttt.transparenthealth.org").substring(0,  4), "MIID");
		} catch (TextParseException e) {
			e.printStackTrace();
			fail();
		}
	}

}
