/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

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
