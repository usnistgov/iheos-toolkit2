package gov.nist.direct.bouncycastleCodeExamples;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.mail.smime.SMIMEEnvelopedParser;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.mail.smime.examples.ExampleUtils;
import org.bouncycastle.mail.smime.util.SharedFileInputStream;

/**
 * This class originates from the Bouncycastle package and was modified by NIST
 * developers for the purpose of generating messages in the context of the DIRECT
 * project.
 * 
 * a simple example that reads an encrypted email using the large file model.
 * <p>
 * The key store can be created using the class in
 * org.bouncycastle.jce.examples.PKCS12Example - the program expects only one
 * key to be present.
 */


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
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
*/

public class ReadLargeEncryptedMail
{  

	public String keystore;
	public String psw;
	public String output;
	public String input;

	public ReadLargeEncryptedMail(){
		keystore = "";
		psw = "";
		output = "";
		input = "";
	}


	public ReadLargeEncryptedMail(String inputFile, String pkcs12Keystore, String password, String outputFile){
		keystore = pkcs12Keystore;
		psw = password;
		output = outputFile;
		input = inputFile;
	}


	@SuppressWarnings("deprecation")
	public void readLargeEncryptedMail() throws Exception {

		//
		// Open the key store
		//
		KeyStore    ks = KeyStore.getInstance("PKCS12", "BC");
		String      keyAlias = ExampleUtils.findKeyAlias(ks, keystore, psw.toCharArray());


		//
		// find the certificate for the private key and generate a 
		// suitable recipient identifier.
		//
		X509Certificate cert = (X509Certificate)ks.getCertificate(keyAlias);
		RecipientId     recId = new JceKeyTransRecipientId(cert);

		//
		// Get a Session object with the default properties.
		//         
		Properties props = System.getProperties();

		Session session = Session.getDefaultInstance(props, null);

		MimeMessage msg = new MimeMessage(session, new SharedFileInputStream(input));

		SMIMEEnvelopedParser       m = new SMIMEEnvelopedParser(msg);

		RecipientInformationStore   recipients = m.getRecipientInfos();
		RecipientInformation        recipient = recipients.get(recId);

		MimeBodyPart        res = SMIMEUtil.toMimeBodyPart(recipient.getContentStream(new JceKeyTransEnvelopedRecipient((PrivateKey)ks.getKey(keyAlias, null)).setProvider("BC")));

		ExampleUtils.dumpContent(res, output);


	}
}
