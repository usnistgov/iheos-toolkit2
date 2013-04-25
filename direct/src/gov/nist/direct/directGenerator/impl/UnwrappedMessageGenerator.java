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

package gov.nist.direct.directGenerator.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import gov.nist.direct.directGenerator.DirectMessageGenerator;
import gov.nist.direct.directGenerator.MessageGeneratorUtils;
import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.direct.messageProcessor.cert.PublicCertLoader;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;

public class UnwrappedMessageGenerator implements DirectMessageGenerator {

	public UnwrappedMessageGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MimeMessage generateMessage(byte[] signingCert, String signingCertPw, String subject, 
			String textMessage, File attachmentContentFile, String fromAddress, String toAddress, byte[] encryptionCertBA) throws Exception {
		Security.addProvider(new BouncyCastleProvider());

//		try{
			ByteArrayInputStream signatureCert = new ByteArrayInputStream(signingCert);
			CertificateLoader loader = new CertificateLoader(signatureCert, signingCertPw);
			
			SMIMESignedGenerator gen = loader.getSMIMESignedGenerator();
			
			//
			// create the base for our message
			//
			MimeBodyPart    msg1 = new MimeBodyPart();

			MimeMultipart mp = new MimeMultipart();

			mp.addBodyPart(MessageGeneratorUtils.addText(textMessage));
	        mp.addBodyPart(MessageGeneratorUtils.addAttachement(attachmentContentFile));

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

//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
	}

}
