package gov.nist.direct.messageGenerator;

import java.io.File;

import javax.mail.internet.MimeMessage;

public interface MessageGenerator {

	
	public MimeMessage generateMessage(byte[] signingCert, String signingCertPw, String subject, 
			String textMessage, File attachmentContentFile, String fromAddress, String toAddress, byte[] encryptionCertBA);
}
