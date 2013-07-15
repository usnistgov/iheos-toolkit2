package gov.nist.direct.mdn.generate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Properties;

import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.direct.messageProcessor.cert.PublicCertLoader;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gwt.dev.jjs.ast.JField.Disposition;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.base.mail.MimeMultipartReport;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

/**
 * Class <code>MDNFactory</code> creates MimeMultipartReports containing
 * Message Delivery Notifications as specified by RFC 3798.
 * This class is based on code from the open source Apache James server.
 * https://www.java2s.com/Open-Source/Java/Web-Mail/james-2.3.1/org/apache/james/util/mail/mdn
 *
 * @author jnp3
 * @author dazais
 *
 */
public class MDNGenerator {


	/**
	 * Answers with a MimeMultipartReport containing a
	 * Message Delivery Notification as specified by RFC 2298.
	 * 
	 * @param humanText
	 * @param reporting_UA_name
	 * @param reporting_UA_product
	 * @param original_recipient (optional)
	 * @param final_recipient
	 * @param original_message_id
	 * @param disposition
	 * @return MimeMultipartReport
	 * @throws MessagingException
	 */
	public static MimeMultipartReport create(String humanText,
			String reporting_UA_name,
			String reporting_UA_product,
			String original_recipient,
			String final_recipient,
			String original_message_id,
			Disposition disposition
			) throws MessagingException {




		// Create the message parts. According to RFC 2298, there are two
		// compulsory parts and one optional part...
		MimeMultipartReport multiPart = new MimeMultipartReport();
		multiPart.setReportType("disposition-notification");

		// Part 1: The 'human-readable' part
		MimeBodyPart humanPart = new MimeBodyPart();
		humanPart.setText(humanText);
		multiPart.addBodyPart(humanPart);

		// Part 2: MDN Report Part
		// 1) reporting-ua-field
		StringBuffer mdnReport = new StringBuffer(128);
		mdnReport.append("Reporting-UA: ");
		mdnReport.append((reporting_UA_name == null ? "" : reporting_UA_name));
		mdnReport.append("; ");
		mdnReport.append((reporting_UA_product == null ? "" : reporting_UA_product));
		mdnReport.append("\r\n");
		// 2) original-recipient-field - optional
		if (null != original_recipient)
		{
			mdnReport.append("Original-Recipient: ");
			mdnReport.append("rfc822; ");
			mdnReport.append(original_recipient);
			mdnReport.append("\r\n");
		}
		// 3) final-recipient-field
		mdnReport.append("Final-Recepient: ");
		mdnReport.append("rfc822; ");
		mdnReport.append((final_recipient == null ? "" : final_recipient));
		mdnReport.append("\r\n");
		// 4) original-message-id-field
		mdnReport.append("Original-Message-ID: ");
		mdnReport.append((original_message_id == null ? "" : original_message_id));
		mdnReport.append("\r\n");
		// 5) disposition-field
		mdnReport.append("Disposition: ");
		mdnReport.append(disposition.toString());
		mdnReport.append("\r\n");
		MimeBodyPart mdnPart = new MimeBodyPart();
		mdnPart.setContent(mdnReport.toString(), "message/disposition-notification");
		multiPart.addBodyPart(mdnPart);

		// Part 3: The optional third part, the original message is omitted.
		// We don't want to propogate over-sized, virus infected or
		// other undesirable mail!
		// There is the option of adding a Text/RFC822-Headers part, which
		// includes only the RFC 822 headers of the failed message. This is
		// described in RFC 1892. It would be a useful addition!        
		return multiPart;
	}

	public static MimeMessage createSignedAndEncrypted(String humanText,
			String reporting_UA_name,
			String reporting_UA_product,
			String original_recipient,
			String final_recipient,
			String original_message_id,
			Disposition disposition,
			String from,
			String to,
			String subject,
			byte[] encCert, 
			byte[] signCert, 
			String signCertPassword) {

		try {
			ByteArrayInputStream signatureCert = new ByteArrayInputStream(signCert);
			CertificateLoader loader = new CertificateLoader(signatureCert, signCertPassword);

			SMIMESignedGenerator gen = loader.getSMIMESignedGenerator();

			// Sign the message
			MimeBodyPart m = new MimeBodyPart();
			m.setContent(create(humanText, reporting_UA_name, reporting_UA_product, original_recipient, final_recipient, original_message_id, disposition));

			//
			// extract the multipart object from the SMIMESigned object.
			//

			MimeMultipart mm = gen.generate(m);

			//
			// Get a Session object and create the mail message
			//
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props, null);

			Address fromUser = new InternetAddress(new SMTPAddress().properEmailAddr(from));
			Address toUser = new InternetAddress(new SMTPAddress().properEmailAddr(to));

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
			
			// TODO Remove the dump
			body.writeTo(new FileOutputStream("Decrypted_MDN.txt"));


			// Encryption cert
			PublicCertLoader publicLoader = new PublicCertLoader(encCert);
			X509Certificate encryptCert = publicLoader.getCertificate();

			//System.out.println(encCert);

			/* Create the encrypter */
			SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
			try {
				encrypter.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(encryptCert).setProvider("BC"));
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

			return msg;


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
