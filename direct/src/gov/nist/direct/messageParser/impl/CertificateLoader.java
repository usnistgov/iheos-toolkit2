package gov.nist.direct.messageParser.impl;

import gov.nist.direct.validation.MessageValidatorFacade;
import gov.nist.direct.validation.impl.DirectMimeMessageValidatorFacade;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.mail.smime.SMIMEEnveloped;

public class CertificateLoader {

	String keyAlias = "";
	X509Certificate cert;
	PrivateKey pkey;
	
	
	public CertificateLoader(InputStream certificate, String password) throws Exception {
		
		//
		// Open the key store
		//
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("PKCS12", "BC");
		} catch (KeyStoreException e1) {
			throw new KeyStoreException();
		} catch (NoSuchProviderException e1) {
			throw new NoSuchAlgorithmException();
		}

		// Message Validator
		MessageValidatorFacade msgValidator = new DirectMimeMessageValidatorFacade();

		try {
			if(password == null) {
				password="";
			} 
			ks.load(certificate, password.toCharArray());
		} catch (NoSuchAlgorithmException e1) {
			throw new NoSuchAlgorithmException();
		} catch (CertificateException e1) {
			throw new CertificateException();
		} catch (IOException e1) {
			throw new IOException();
		} catch (Exception e1) {
			// Verifying certificate format
			X509Certificate encCert = null;

	        System.out.println("Trying to read as a public certificate");
	        try {
	        	CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509", "BC");
	        	encCert = (X509Certificate)x509CertFact.generateCertificate(certificate);
	        	System.out.println("It is a public certificate");
	        	System.out.println(encCert);
	        } catch (Exception e2) {
	        	System.out.println("It is not a public certificate");
	        }
			throw new Exception();
		}

		@SuppressWarnings("rawtypes")
		Enumeration e = null;
		try {
			e = ks.aliases();
		} catch (KeyStoreException e1) {
			throw new KeyStoreException();
		}
		
		this.keyAlias = null;

		if (e != null) {
			while (e.hasMoreElements())
			{
				String  alias = (String)e.nextElement();

				try {
					if (ks.isKeyEntry(alias))
					{
						keyAlias = alias;
					}
				} catch (KeyStoreException e1) {
					throw new KeyStoreException();
				}
			}
		}

		//
		// find the certificate for the private key and generate a 
		// suitable recipient identifier.
		//
		
		this.cert = null;
		
		try {
			cert = (X509Certificate)ks.getCertificate(keyAlias);
			System.out.println(cert);
		} catch (KeyStoreException e1) {
			throw new KeyStoreException();
		}
		
		pkey = (PrivateKey)ks.getKey(keyAlias, null);

	}
	
	public String getKeyAlias() {
		return this.keyAlias;
	}
	
	public X509Certificate getX509Certificate() {
		return this.cert;
	}
	
	public PrivateKey getPrivateKey() {
		return this.pkey;
	}
	
}
