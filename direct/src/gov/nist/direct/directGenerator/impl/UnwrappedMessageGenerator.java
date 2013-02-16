package gov.nist.direct.directGenerator.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.util.Store;

import gov.nist.direct.directGenerator.DirectMessageGenerator;
import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.direct.messageProcessor.cert.PublicCertLoader;
import gov.nist.direct.x509.X509CertificateEx;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;
import gov.nist.toolkit.utilities.io.Io;

public class UnwrappedMessageGenerator implements DirectMessageGenerator {

	public UnwrappedMessageGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MimeMessage generateMessage(byte[] signingCert, String signingCertPw, String subject, 
			String textMessage, File attachmentContentFile, String fromAddress, String toAddress, byte[] encryptionCertBA) {
		Security.addProvider(new BouncyCastleProvider());

		try{
			ByteArrayInputStream signatureCert = new ByteArrayInputStream(signingCert);
			CertificateLoader loader = new CertificateLoader(signatureCert, signingCertPw);
			
			SMIMESignedGenerator gen = loader.getSMIMESignedGenerator();
			
			//
			// create the base for our message
			//
			MimeBodyPart    msg1 = new MimeBodyPart();

			msg1.setText(textMessage);

			//File contentFile = new File("/var/lib/tomcat_ttt/webapps/ttt/pubcert/CCDA_CCD_b1_Ambulatory.xml");

			byte[] fileContent = FileUtils.readFileToByteArray(attachmentContentFile);
			byte[] content = Base64.encodeBase64(fileContent);


			InternetHeaders partHeaders = new InternetHeaders();
			partHeaders.addHeader("Content-Type", "text/xml; name="+attachmentContentFile.getName());
			partHeaders.addHeader("Content-Transfer-Encoding", "base64");
			partHeaders.addHeader("Content-Disposition", "attachment; filename="+attachmentContentFile.getName());

			MimeBodyPart ccda = new MimeBodyPart(partHeaders, content);

			MimeMultipart mp = new MimeMultipart();

			mp.addBodyPart(msg1);
			mp.addBodyPart(ccda);

			MimeBodyPart m = new MimeBodyPart();
			m.setContent(mp);

			//
			// extract the multipart object from the SMIMESigned object.
			//

			MimeMultipart mm = gen.generate(m);


			/*OutputStream ostmp = new FileOutputStream(new File("/Users/bill/tmp/direct.send.txt"));
        	String ctype = mm.getContentType();
        	ostmp.write(ctype.getBytes());
        	ostmp.write(new String("\r\n\r\n").getBytes());
        	mm.writeTo(ostmp);*/

			//
			// Get a Session object and create the mail message
			//
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props, null);

			Address fromUser = new InternetAddress(new SMTPAddress().properEmailAddr(fromAddress));
			Address toUser = new InternetAddress(new SMTPAddress().properEmailAddr(toAddress));

			MimeBodyPart body = new MimeBodyPart();
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			try
			{
				mm.writeTo(oStream);
				oStream.flush();
				InternetHeaders headers = new InternetHeaders();
				headers.addHeader("Content-Type", mm.getContentType());




				body = new MimeBodyPart(headers, oStream.toByteArray());
				IOUtils.closeQuietly(oStream);

			}    

			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}


			// Encryption cert
			PublicCertLoader publicLoader = new PublicCertLoader(encryptionCertBA);
			X509Certificate encCert = publicLoader.getCertificate();

			//System.out.println(encCert);

			/* Create the encrypter */
			SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
			try {
				encrypter.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(encCert).setProvider("BC"));
			} catch (Exception e1) {
				throw new Exception("Error loading encryption cert - must be in X.509 format", e1);
			}
			/* Encrypt the message */
			MimeBodyPart encryptedPart = encrypter.generate(body,
					// RC2_CBC
					new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC").build());

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(fromUser);
			msg.setRecipient(Message.RecipientType.TO, toUser);
			msg.setSentDate(new Date());
			msg.setSubject(subject);
			msg.setContent(encryptedPart.getContent(), encryptedPart.getContentType());
			msg.setDisposition("attachment");
			msg.setFileName("smime.p7m");
			msg.saveChanges();

			/*
			OutputStream ostmp1 = new FileOutputStream(new File("/var/lib/tomcat_ttt/webapps/ttt/pubcert/encrypted3.txt"));
        	msg.writeTo(ostmp1);
        	OutputStream ostmp2 = new FileOutputStream(new File("/var/lib/tomcat_ttt/webapps/ttt/pubcert/encrypted3_body.txt"));
        	body.writeTo(ostmp2);
			 */

			return msg;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
