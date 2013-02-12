package gov.nist.direct.messageProcessor.cert;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PublicCertLoader {
	
	X509Certificate encCert;
	
	public PublicCertLoader(byte[] certificate) throws Exception {
		// Encryption cert
		encCert = null;

		ByteArrayInputStream is;
		try {
			is = new ByteArrayInputStream(certificate);
			CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509");
			encCert = (X509Certificate)x509CertFact.generateCertificate(is);
		} catch (Exception e1) {
			throw new Exception("Error loading X.509 encryption cert - probably wrong format", e1);
		}
	}
	
	public X509Certificate getCertificate() {
		return this.encCert;
	}

}
