package gov.nist.toolkit.testengine.transactions;

import gov.nist.direct.directGenerator.impl.UnwrappedMessageGenerator;
import gov.nist.direct.directGenerator.impl.WrappedMessageGenerator;
import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.MessageLogManager;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.directsupport.SMTPException;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DirectTransaction extends BasicTransaction {
	File certFile = null;
	String certFilePassword = null;
	File bodyContentFile = null;
	String bodyContent = null;
	String fromAddress = null;
	String toAddress = null;
	String mdnAddress = null;
	String subject = null;
	File attachmentContentFile = null;
	String mailerHostname = null;
	int mailerPort = 25;
	boolean useSMTPProtocol = true;
	boolean sendWrapped = true;

	static final Logger logger = Logger.getLogger(DirectTransaction.class);


	static { 
		// add BC in case it isn't yet a valid security provider 
		Security.addProvider(new BouncyCastleProvider()); 
	} 

	public DirectTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void run(OMElement request) throws Exception {

		verifyParameters();

		if (!s_ctx.getStatus())
			return;
		
		Address fromUser = new InternetAddress(new SMTPAddress().properEmailAddr(fromAddress));
		Address toUser = new InternetAddress(new SMTPAddress().properEmailAddr(toAddress));
		
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage msg = new MimeMessage(session);
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		
		if (sendWrapped) {
			msg = createWrapedSendMail(toAddress, fromAddress);
		} else {
			msg = createSendMail(toAddress, fromAddress);
		}
		
		System.out.println("WRAPPED? : " + sendWrapped);
		
		logger.info("MessageId="+ msg.getMessageID());
		
		String messageID = Utils.rawMsgId(msg.getMessageID());

		MessageLogManager.logDirectMessage(transactionSettings.user, new Date(), ls.getDIRECT_SEND_FOLDER(), ls.getDIRECT_MESSAGE_FOLDER(), messageID, msg, "");
		
		// Second log under the To email folder
		MessageLogManager.logDirectSendMessage(toAddress, new Date(), ls.getDIRECT_SEND_FOLDER(), ls.getDIRECT_MESSAGE_FOLDER(), messageID, msg, "", transactionSettings.user);

		/*InputStream is2 = new FileInputStream(new File("/var/lib/tomcat_ttt/webapps/ttt/pubcert/encrypted3.txt"));
		msg = new MimeMessage(session, is2);*/
		
        //OutputStream ostmp1 = new FileOutputStream(new File("/var/lib/tomcat_ttt/webapps/ttt/pubcert/encrypted_before_sending.txt"));
        //msg.writeTo(ostmp1);

		// For test purpose NEED to be removed
		// TODO        
		//msg.writeTo(new FileOutputStream("encrypted.txt"));

		logger.debug("Opening socket to Direct system on " + mailerHostname + ":" + mailerPort + "...");
		Socket socket;
		try {
			socket = new Socket(mailerHostname, mailerPort);
		} catch (UnknownHostException e) {
			s_ctx.set_error("Error connecting to " + mailerHostname + ":" + mailerPort + " - " + e.getMessage());
			return;
		} catch (IOException e) {
			s_ctx.set_error("Error connecting to " + mailerHostname + ":" + mailerPort + " - " + e.getMessage());
			return;
		}
		logger.debug("\t...Success");

		// org.bouncycastle.cms.CMSException: exception wrapping content key: 
		//      cannot create cipher: No such algorithm: 1.2.840.10040.4.1

		//1.2.840.10040.4.1 is the OID for DSA, but as it says there is no Cipher 
		//for it. This is because DSA can only be used for signing - you cannot 
		//use it to encrypt with. 

		try {
			if (useSMTPProtocol) {
				// fromUser.toString() does the parsing for me so I don't need my code. 
				smtpProtocol(socket, msg, "hit-testing.nist.gov", fromUser.toString(), toUser.toString());
			} else {
				OutputStream os;
				os = socket.getOutputStream();
				msg.writeTo(os);
				os.flush();
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		} finally {
			socket.close();
		}
	}

	static final String CRLF = "\r\n";
	BufferedReader in = null;
	BufferedOutputStream out = null;

	void smtpProtocol(Socket socket, MimeMessage mmsg, String domainname, String from, String to) throws Exception {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedOutputStream(socket.getOutputStream());

		try {
			from = new SMTPAddress().properEmailAddr(from);
			to = new SMTPAddress().properEmailAddr(to);

			rcv("220");

			send("HELO " + domainname);

			rcv("250"); 

			send("MAIL FROM:" + from);

			rcv("250"); 

			send("RCPT TO:" + to);

			rcv("250");

			send("DATA");

			rcv("354"); 

			send("Subject: " + subject);

			mmsg.writeTo(out);

			send(CRLF + ".");

			rcv("250");

			send("QUIT");

			rcv("221"); 
		} catch (Exception e) {
			logger.error("Protocol error: " + ExceptionUtil.exception_details(e));
			throw new Exception("Protocol error: ", e);
		} finally {
			in.close();
			out.close();
			in = null;
			out = null;
		}

	}

	void send(String cmd) throws IOException {
		logger.debug("SMTP SEND: " + cmd);
		cmd = cmd + CRLF;
		out.write(cmd.getBytes());
		out.flush();
	}

	String rcv(String expect) throws IOException, SMTPException {
		String msg;
		msg = in.readLine();
		logger.debug("SMTP RCV: " + msg);
		if (expect != null && !msg.startsWith(expect))
			throw new SMTPException("Error: expecting " + expect + ", got <" + msg + "> instead");
		return msg;
	}



//	String escapeInternetAddress(String in) {
//		String x = in.trim();
//		if (x.length() > 0 && x.charAt(0) != '"')
//			return "\"" + x + "\"";
//		return in;
//	}

	void verifyParameters() throws XdsInternalException  {
		List<String> errors = new ArrayList<String>();

		if (certFile == null) {
			try {
				s_ctx.set_error("CertFile parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (certFilePassword == null) {
			try {
				s_ctx.set_error("CertFilePassword parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (bodyContent == null) {
			try {
				s_ctx.set_error("BodyContentFile and BodyContent parameters missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (fromAddress == null) {
			try {
				s_ctx.set_error("DirectFromAddress parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (toAddress == null) {
			try {
				s_ctx.set_error("DirectToAddress parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (subject == null) {
			try {
				s_ctx.set_error("Subject parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (attachmentContentFile == null) {
			try {
				s_ctx.set_error("AttachmentContentFile parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}
		if (mailerHostname == null) {
			try {
				s_ctx.set_error("DirectSystemMailerHostname parameter missing");
			} catch (XdsInternalException e) {
				errors.add(e.getMessage());
			}
		}

		if (errors.size() > 0)
			throw new XdsInternalException(errors.toString());
	}

	@Override
	protected void parseInstruction(OMElement part)
			throws XdsInternalException, MetadataException {
		String part_name = part.getLocalName();
		if (part_name.equals("CertFile")) {
			if (certFile == null) {
				certFile = new File(testConfig.testplanDir + File.separator + part.getText());
			}
			if (!certFile.exists())
				s_ctx.set_error("CertFile points to file that does not exist, " + certFile.toString());
		} 
		else if (part_name.equals("CertFilePassword")) {
			if (certFilePassword == null)
				certFilePassword = part.getText();
		}
		else if (part_name.equals("Wrapped")) {
			sendWrapped = false;
			String val = part.getText();
			if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes"))
				sendWrapped = true;
		}
		else if (part_name.equals("BodyContent")) {
			bodyContent = part.getText();
		}
		else if (part_name.equals("BodyContentFile")) {
			bodyContentFile = new File(testConfig.testplanDir + File.separator + part.getText());
			if (!bodyContentFile.exists())
				s_ctx.set_error("BodyContentFile points to file that does not exist, " + bodyContentFile.toString());
			try {
				bodyContent = Io.stringFromFile(bodyContentFile);
			} catch (IOException e) {
				throw new XdsInternalException("Cannot load BodyContent", e);
			}
		}
		else if (part_name.equals("DirectFromAddress")) {
			fromAddress = part.getText();
		}
		else if (part_name.equals("DirectToAddress")) {
			toAddress = part.getText();
		}
		else if (part_name.equals("MDNAddress")) {
			mdnAddress = part.getText();
		}
		else if (part_name.equals("DirectSystemMailerPort")) {
			try {
				mailerPort = Integer.parseInt(part.getText());
			} catch (NumberFormatException e) {
				s_ctx.set_error("MailerPort value, " + part.getText() + ", cannot be parsed as an Integer");
			}
		}
		else if (part_name.equals("Subject")) {
			subject = part.getText();
		}
		else if (part_name.equals("AttachmentContentFile")) {
			String text = part.getText();
			if (text.startsWith("/") || text.charAt(1) == ':')
				attachmentContentFile = new File(text);
			else
				attachmentContentFile = new File(testConfig.testplanDir + File.separator + text);
			if (!attachmentContentFile.exists()) {
				File acf2 = new File(text);
				if (!acf2.exists()) {
					s_ctx.set_error("AttachmentContentFile points to file that does not exist, " + attachmentContentFile.toString());
				} else {
					attachmentContentFile = acf2;
				}
			}
		}
		else if (part_name.equals("DirectSystemMailerHostname")) {
			mailerHostname = part.getText();
			/*DnsLookup lookup = new DnsLookup();
			try {
				mailerHostname = lookup.getMxRecord(part.getText());
			} catch (TextParseException e) {
				s_ctx.set_error("Cannot get the mailer hostname " + e.toString());
				e.printStackTrace();
			}*/
		}
		else
			parseBasicInstruction(part);
	}

	@Override
	protected String getRequestAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getBasicTransactionName() {
		return "Direct";
	}
	
	public MimeMessage createSendMail(String toAddress, String fromAddress) throws Exception {
		logger.info("Generating unwrapped format");
		// Get the signing certificate
		Map<String, Object> extra2 = planContext.getExtraLinkage2();
		byte[] signingCert = (byte[]) extra2.get("signingCert");
		// Get the signing certificate password
		Object signingCertPwO = planContext.getExtraLinkage().get("signingCertPassword");
		String signingCertPw = (signingCertPwO == null) ? "" : signingCertPwO.toString();
		
		// Get the encryption certificate
		byte[] encryptionCertBA = (byte[]) planContext.getExtraLinkage2().get("encryptionCert");
		
		String textMessage = "Message test";
		String subject = "Message test";
		
		UnwrappedMessageGenerator gen = new UnwrappedMessageGenerator();
		return gen.generateMessage(signingCert, signingCertPw, textMessage, subject, attachmentContentFile, fromAddress, toAddress, encryptionCertBA);
	}
	
	public MimeMessage createWrapedSendMail(String toAddress, String fromAddress) throws Exception {
		logger.info("Generating wrapped format");
		// Get the signing certificate
		Map<String, Object> extra2 = planContext.getExtraLinkage2();
		byte[] signingCert = (byte[]) extra2.get("signingCert");
		// Get the signing certificate password
		Object signingCertPwO = planContext.getExtraLinkage().get("signingCertPassword");
		String signingCertPw = (signingCertPwO == null) ? "" : signingCertPwO.toString();        

		// Get the encryption certificate
		byte[] encryptionCertBA = (byte[]) planContext.getExtraLinkage2().get("encryptionCert");

		String textMessage = "Message test";
		String subject = "Message test";

		WrappedMessageGenerator gen = new WrappedMessageGenerator();
		return gen.generateMessage(signingCert, signingCertPw, textMessage, subject, attachmentContentFile, fromAddress, toAddress, encryptionCertBA);
	}

}

