package gov.nist.toolkit.dns.test;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
			String test = new DnsLookup().getCertRecord("ttt.transparenthealth.org");
			
			String base64 = test.replaceAll("\\s", ""); 
			base64 = base64.replace("-----BEGINPKCS7-----", ""); 
			base64 = base64.replace("-----ENDPKCS7-----", ""); 
			byte[] derFile = org.bouncycastle.util.encoders.Base64.decode(base64.getBytes());
			
			X509Certificate encCert = null;

			ByteArrayInputStream is;
			is = new ByteArrayInputStream(derFile);
			CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509");
			encCert = (X509Certificate)x509CertFact.generateCertificate(is);;
			
			System.out.println(encCert);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			assertEquals( new DnsLookup().getCertRecord("ttt.transparenthealth.org").substring(0,  4), "MIID");
		} catch (TextParseException e) {
			e.printStackTrace();
			fail();
		}
	}

}
