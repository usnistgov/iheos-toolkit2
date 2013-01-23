package gov.nist.direct.bouncycastleCodeExamples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;

/**
 * This class originates from the Bouncycastle package and was modified by NIST
 * developers for the purpose of generating messages in the context of the DIRECT
 * project.
 * 
 * 
 * a simple example that creates a single encrypted mail message.
 * <p>
 * The key store can be created using the class in
 * org.bouncycastle.jce.examples.PKCS12Example - the program expects only one
 * key to be present in the key file.
 * <p>
 * Note: while this means that both the private key is available to
 * the program, the private key is retrieved from the keystore only for
 * the purposes of locating the corresponding public key, in normal circumstances
 * you would only be doing this with a certificate available.
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

public class CreateEncryptedMail {
    
	private String certFilename;
	private String password;
	private String outputFile;
	private String name;
	private String email;
	private String subject;
	private String content;
	
	public CreateEncryptedMail() {
		this.certFilename = "";
		this.password = "";
		this.outputFile = "";
		this.name = "";
		this.email = "";
		this.subject = "";
		this.content = "";
	}

	public CreateEncryptedMail(String certFilename, String password, String outputFile) {
		this.certFilename = certFilename;
		this.password = password;
		this.outputFile = outputFile;
		this.name = "";
		this.email = "";
		this.subject = "";
		this.content = "";
	}
	
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void createEncryptedMail() throws Exception {
    	Security.addProvider(new BouncyCastleProvider());
        //
        // Open the key store
        //
        KeyStore    ks = KeyStore.getInstance("PKCS12", "BC");

        ks.load(new FileInputStream(certFilename), password.toCharArray());

        Enumeration e = ks.aliases();
        String      keyAlias = null;

        while (e.hasMoreElements())
        {
            String  alias = (String)e.nextElement();

            if (ks.isKeyEntry(alias))
            {
                keyAlias = alias;
            }
        }

        if (keyAlias == null)
        {
            System.err.println("can't find a private key!");
            System.exit(0);
        }

        Certificate[]   chain = ks.getCertificateChain(keyAlias);

        //
        // create the generator for creating an smime/encrypted message
        //
        SMIMEEnvelopedGenerator  gen = new SMIMEEnvelopedGenerator();
          
        gen.addKeyTransRecipient((X509Certificate)chain[0]);

        //
        // create a subject key id - this has to be done the same way as
        // it is done in the certificate associated with the private key
        // version 3 only.
        //
        /*
        MessageDigest           dig = MessageDigest.getInstance("SHA1", "BC");

        dig.update(cert.getPublicKey().getEncoded());
              
        gen.addKeyTransRecipient(cert.getPublicKey(), dig.digest());
        */
         
        //
        // create the base for our message
        //
        MimeBodyPart    msg = new MimeBodyPart();

        msg.setText("Hello world!");

        MimeBodyPart mp = gen.generate(msg, SMIMEEnvelopedGenerator.RC2_CBC, "BC");
        //
        // Get a Session object and create the mail message
        //
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);

        Address fromUser = new InternetAddress("\"<Test Corp.>\"<tets@corp.org>");
        Address toUser = new InternetAddress("example@corp.org");

        MimeMessage body = new MimeMessage(session);
        body.setFrom(fromUser);
        body.setRecipient(Message.RecipientType.TO, toUser);
        body.setSubject("example encrypted message");
        body.setContent(mp.getContent(), mp.getContentType());
        body.saveChanges();

        body.writeTo(new FileOutputStream(outputFile));
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
