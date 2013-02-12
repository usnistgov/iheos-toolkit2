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
import java.util.Map;
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

		try {
		//
		// set up our certs
		//
		KeyPairGenerator    kpg  = KeyPairGenerator.getInstance("RSA", "BC");

		kpg.initialize(1024, new SecureRandom());

		//List                certList = new ArrayList();

		//
        // Open the key store
        //
        KeyStore    ks = KeyStore.getInstance("PKCS12", "BC");      

        try {
        	ks.load(Io.bytesToInputStream(signingCert), signingCertPw.toCharArray());
        } catch (Throwable e) {
        	throw new Exception("Signing private key may be in wrong format, PKCS12 expected", e);
        }

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
		// cert that issued the signing certificate
		//
        String              signDN = ((X509Certificate) chain[0]).getIssuerDN().toString();
		
		//certList.add(chain[0]);
        

		//
		// be careful about setting extra headers here. Some mail clients
		// ignore the To and From fields (for example) in the body part
		// that contains the multipart. The result of this will be that the
		// signature fails to verify... Outlook Express is an example of
		// a client that exhibits this behaviour.
		//

        Collection<X509Certificate> signingCertificates = new ArrayList<X509Certificate>();
        X509CertificateEx signCert = X509CertificateEx.fromX509Certificate((X509Certificate) chain[0], (PrivateKey)ks.getKey(keyAlias, "".toCharArray()));
        
        //System.out.println(signCert);
        
        signingCertificates.add(signCert);

		//
		// create a CertStore containing the certificates we want carried
		// in the signature
		//
		Store certs = new JcaCertStore(signingCertificates);

		//
		// create some smime capabilities in case someone wants to respond
		//
		ASN1EncodableVector         signedAttrs = new ASN1EncodableVector();
		SMIMECapabilityVector       caps = new SMIMECapabilityVector();

		caps.addCapability(SMIMECapability.dES_EDE3_CBC);
		caps.addCapability(SMIMECapability.rC2_CBC, 128);
		caps.addCapability(SMIMECapability.dES_CBC);
		caps.addCapability(new ASN1ObjectIdentifier("1.2.840.113549.1.7.1"));
		caps.addCapability(new ASN1ObjectIdentifier("1.2.840.113549.1.9.22.1"));

		signedAttrs.add(new SMIMECapabilitiesAttribute(caps));
		
		//logger.debug("Signing Cert is \n = " + signCert.toString());
		//
		// add an encryption key preference for encrypted responses -
		// normally this would be different from the signing certificate...
		//
		IssuerAndSerialNumber   issAndSer = new IssuerAndSerialNumber(
				new X500Name(signDN), signCert.getSerialNumber());

		signedAttrs.add(new SMIMEEncryptionKeyPreferenceAttribute(issAndSer));

		//
		// create the generator for creating an smime/signed message
		//
		SMIMESignedGenerator gen = new SMIMESignedGenerator();

		//
		// add a signer to the generator - this specifies we are using SHA1 and
		// adding the smime attributes above to the signed attributes that
		// will be generated as part of the signature. The encryption algorithm
		// used is taken from the key - in this RSA with PKCS1Padding
		//
		gen.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").setSignedAttributeGenerator(new AttributeTable(signedAttrs)).build("SHA1withRSA", signCert.getPrivateKey(), signCert));

		//
		// add our pool of certs and cerls (if any) to go with the signature
		//
		gen.addCertificates(certs);
		
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


		//
		// Open the key store
		//
		/*
		KeyStore    ks = KeyStore.getInstance("PKCS12", "BC");

		ks.load(new FileInputStream(certFile.toString()), certFilePassword.toCharArray());

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
		*/
		
        // Encryption cert
        X509Certificate encCert = null;

        ByteArrayInputStream is;
        try {
        	is = new ByteArrayInputStream(encryptionCertBA);
        	CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509");
        	encCert = (X509Certificate)x509CertFact.generateCertificate(is);
        } catch (Exception e1) {
        	throw new Exception("Error loading X.509 encryption cert - probably wrong format", e1);
        }

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
