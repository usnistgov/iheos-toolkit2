package gov.nist.toolkit.directsim;

import gov.nist.messageDispatch.MessageDispatchUtils;
import gov.nist.toolkit.actorfactory.DirectActorFactory;
import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.directsupport.SMTPException;
import gov.nist.toolkit.email.Emailer;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simulators.support.ValidateMessageService;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.TkPropsServer;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.MessageValidatorDisplay;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.HtmlValFormatter;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

/**
 * Title:        Sample Server
 * Description:  This utility will accept input from a socket, posting back to the socket before closing the link.
 * It is intended as a template for coders to base servers on. Please report bugs to brad at kieser.net
 * Copyright:    Copyright (c) 2002
 * Company:      Kieser.net
 * @author B. Kieser
 * @version 1.0
 */

public class Listener {

	private static int port=4444, maxConnections=0;
	static String externalCache;
	static String pathToPrivateKey;

	static Logger logger = Logger.getLogger(Listener.class);

	static {
		MessageValidatorFactory fact = new MessageValidatorFactory();
	}


	// Listen for incoming connections and handle them
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Usage: Listener <port> <toolkit-external-cache> <keystore holding private key>");
			System.exit(-1);
		}
		try {
			port = Integer.parseInt(args[0]);
			externalCache = args[1];
			pathToPrivateKey = args[2];
		} catch (Exception e) {
			System.out.println("Usage: Listener <port> <toolkit-external-cache> <keystore holding private key>: " + e.getMessage());
			System.exit(-1);
		}

		Installation.installation().externalCache(new File(externalCache));

		int i=0;

		try{
			ServerSocket listener = new ServerSocket(port);
			Socket server;

			while((i++ < maxConnections) || (maxConnections == 0)){
				server = listener.accept();
				doComms conn_c= new doComms(server, new File(externalCache), pathToPrivateKey);
				Thread t = new Thread(conn_c);
				t.start();
			}
		} 
		catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
		catch (Exception ioe) {
			System.out.println("Exception on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}

}

class doComms implements Runnable {
	Socket server;
	File externalCache;
	String pathToPrivateKey;
	String mailFrom = null;
	boolean logInputs = true;

	static Logger logger = Logger.getLogger(doComms.class);

	doComms(Socket server, File externalCache, String pathToPrivateKey) {
		this.server=server;
		this.externalCache = externalCache;
		this.pathToPrivateKey = pathToPrivateKey;
	}

	public void run () {
		StringBuffer message = new StringBuffer();

		InetAddress ia = server.getInetAddress();
		logger.info("Connection from " + ia.getHostName() + " (" + ia.getHostAddress() + ")");

		File propFile = new File(externalCache + File.separator +  "tk_props.txt");
		logger.debug("Loading properties from " + propFile);
		TkPropsServer reportingProps = null;
		try {
			reportingProps = TkLoader.LOAD(propFile);
			logger.debug("Properties are\n" + reportingProps.toString());
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			String m = readIncomingSMTPMessage(server, reportingProps.get("direct.toolkit.smtp.domain"));   //"smtp.hit-testing.nist.gov");
			message.append(m);
		} catch (EOFException e) {

		} catch (IOException ioe) {
			logger.error("IOException on socket listen: " + ioe);
			return;
		} catch (SMTPException e) {
			logger.error("SMTPException on socket listen: " + e);
			return;
		} catch (PropertyNotFoundException e) {
			logger.error("tk_props property direct.toolkit.smtp.domain not found: " + e);
			return;
		}
		finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		MimeMessage mmsg;
		boolean isMdn;
		try {
			mmsg = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null), Io.bytesToInputStream(message.toString().getBytes()));
			isMdn = MessageDispatchUtils.isMDN(mmsg);
		} catch (MessagingException e2) {
			logger.error("Message fails MimeMessage parser");
			return;
		}

		HtmlValFormatter hvf = new HtmlValFormatter();

		logger.info("Processing message from " + mailFrom);

		List<String> headers = headers(message);

		String fromHeader = header(headers, "From");
		String toHeader = header(headers, "To");

		if (fromHeader.equals("")) {
			logger.error("No From header found");
			hvf.addError("No From header found");
			return;  // do not process
		}
		if (toHeader.equals("")) {
			logger.error("No To header found");
			hvf.addError("No To header found");
			return; // do not process
		}

		String from = null;
		try {
			from = new SMTPAddress().parseEmailAddr(fromHeader);
		} catch (Throwable e) {
			// no decent fromHeader, cannot even send report back
			String msg = "From Header:  " + e.getMessage();
			logger.error(msg);
			return;
		}

		if (from.equals("")) {
			logger.error("From Addr empty or missing");
			hvf.addError("From Addr empty or missing");
			return;
		}

		logger.info("Necessary headers found");

		// Valid Direct (From) addr?
		DirectUserManager dum = new DirectUserManager();
		if (!dum.directUserExists(from)) {
			// looks like spam - unregistered direct (from) Addr
			logger.error("Throw away message from " + from + " not a registered Direct (From) email account name");
			return;
		}

		logger.debug("Direct addr (From) " + from + " is good");

		StringBuffer validationReport = new StringBuffer();
		validationReport.append("<html><head><title>Validation Results</title></head><body>");
		validationReport.append("<h1>Validation Results</h1>");
		validationReport.append("<p>Valiation from " + new Date().toString() + "</p>");


		MessageValidationResults mvr = null;
		String contactAddr = null;

		// Load only the relevant properties
		reportingProps = reportingProps.withPrefixRemoved("direct.reporting");

		logger.debug("reportingProperties:\n" + reportingProps.toString() + "\n");

		// Reporting enclosure - throw ReportException to flush messages and continue
		try {


			// Get Contact Addr
			DirectRegistrationManager drm = new DirectRegistrationManager(externalCache);
			DirectRegistrationDataServer drd = null;

			try {
				drd = drm.load(from);
			} catch (Exception e) {
				hvf.h2("Error");
				hvf.addError("Internal error loading configuration for Direct address (From) " + from +
						e.getMessage());
			}
			contactAddr = drd.contactAddr;

			ContactRegistrationData crd = dum.contactRegistrationData(contactAddr);

			String to = null;
			try {
				to = new SMTPAddress().parseEmailAddr(toHeader);
			} catch (Throwable e) {
				logger.error("Cannot parse To Header:  " + ExceptionUtil.exception_details(e));
				hvf.addError("Cannot parse To Header:  " + e.getMessage());
				throw new ReportException();
			}

			if (to.equals("")) {
				logger.error("To Addr empty or missing");
				hvf.addError("To Addr empty or missing");
				throw new ReportException();
			}

			//			byte[] privKey = crd.getCert(from);
			logger.info("Loading private key (decryption) from " + pathToPrivateKey);
			byte[] privKey = Io.bytesFromFile(new File(pathToPrivateKey));  // This will be used to un-envelope the part so the private key is needed

			if (privKey == null) {
				hvf.h2("Error");
				//				hvf.addError("Do not have Public Certificate registered for Direct address (From) " + from);
				hvf.addError("Cannot load private key");
			}


			TkPropsServer ccdaProps = reportingProps.withPrefixRemoved("ccdatype");
			String ccdaType = null;
			for (int i=1; i<50; i++) {
				String en = Integer.toString(i);
				String type = ccdaProps.get("type" + en, null);
				String ccdaTo = ccdaProps.get("directTo" + en, null);
				if (type == null || ccdaTo == null)
					break;
				if (ccdaTo.equals(to)) {
					ccdaType = type;
					break;
				}
			}

			// ccdaType tells us the document type to validate against

			// Validate
			// yadda, yadda, yadda
			byte[] messageBytes = message.toString().getBytes();
			GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
			ValidateMessageService vms = new ValidateMessageService(null, null);
			String simpleToHeader = simpleEmailAddr(toHeader);
			logger.debug("toHeader is " + toHeader);
			logger.debug("    which reduces to " + simpleToHeader);
			
			ValidationContext vc = new ValidationContext();
			vc.isDIRECT = true;
			vc.updateable = true;
			vc.ccdaType = ccdaValidationType(reportingProps, simpleToHeader);
			vc.privKey = privKey;
			vc.privKeyPassword = reportingProps.get("privKeyPassword", "");
			
			logger.debug("To: " + simpleToHeader + " translates to ccda type of " + vc.ccdaType);

			mvr = vms.runValidation(vc, null, messageBytes, privKey, gerb);

		} 
		catch (DirectParseException e) {
			logger.error(ExceptionUtil.exception_details(e));
			hvf.h2("Error");
			hvf.addError("Error: " + e.getMessage());
		}
		catch (ReportException re) {
			// nothing to actually do here, more of a goto than an error handling situation
		} 
		catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e));
			hvf.h2("Error");
			hvf.addError("Error: " + e.getMessage());
		}

		MessageValidatorDisplay mvd = new MessageValidatorDisplay(hvf);
		if (mvr != null)
			mvd.displayResults(mvr);


		validationReport.append(hvf.toHtml());

		validationReport.append("</body></html>");

		// Generate validation report URL
		String reportId = new DirectActorFactory().getNewId();
		String url = null;
		File reportFile = null;
		try {
			String baseurl = reportingProps.get("baseurl"); 
			url = 	baseurl +
					((baseurl.endsWith("/") ? "" : "/")) +
					reportId + 
					".html";
			String dir = reportingProps.get("directory");
			reportFile = new File(dir +
					((dir.endsWith(File.separator)) ? "" : File.separator   )  + 
					reportId + 
					".html");
		} catch (PropertyNotFoundException e1) {
			logger.fatal(e1.getMessage());
		}

		// Save it off report behind the URL
		try {
			logger.info("Saving report to " + reportFile);
			logger.info("Report available from " + url);
			Io.stringToFile(reportFile, validationReport.toString());
		} catch (Exception e) {
			logger.error("Cannot create report file " + reportFile);
		}

		// Generate report template
		Map<String, String> subs = new HashMap<String, String>();
		subs.put("%validation.url%", url);
		subs.put("%validation.expiration%", "Later");
		String announcement = reportingProps.linesAsString("validation.template", subs);

		logger.debug("Announcement is:\n" + announcement);

		String announceStr = reportingProps.get("announce", "true");
		boolean announce = (announceStr == null) ? false :  announceStr.compareToIgnoreCase("true") == 0;
		if (announce) {
			try {

				logger.info("Sending report to " + contactAddr);
				if (contactAddr == null) {
					throw new Exception("Internal Error: no Contact email Address found");
				}

				// Send report
				Emailer emailer = new Emailer(reportingProps.withPrefixRemoved("mail"));

				logger.debug("Sending report from " + reportingProps.get("mail.from") + "   to " + contactAddr);
				emailer.sendEmail2(contactAddr, 
						"Direct Message Validation Report", 
						announcement);

			} catch (Exception e) {
				logger.error("Cannot send email (" + e.getClass().getName() + ". " + e.getMessage());
			} catch (Throwable e) {
				logger.error("Cannot send email (" + e.getClass().getName() + ". " + e.getMessage());
			}
		} else {
			logger.info("Validation announcement not sent - disabled in configuration \n" );
		}

		logger.info("Done");


	}

	class RSETException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RSETException() {

		}

	}

	BufferedReader in = null;
	BufferedOutputStream out = null;
	static final String CRLF = "\r\n";
	String domainname = null;

	String readIncomingSMTPMessage(Socket socket, String domainname) throws IOException, SMTPException {
		this.domainname = domainname;
		String buf = null;

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedOutputStream(socket.getOutputStream());

		try {
			send("220 " + domainname + " SMTP Exim");

			buf = rcvStateMachine();

			Log.debug("MESSAGE: \n" + buf);

		} catch (RSETException e) {
			send("250 OK");
			return "";
		} catch (IOException e) {
			return "";
		} finally {
			in.close();
			out.close();
			in = null;
			out = null;
		}

		return buf;
	}

	void send(String cmd) throws IOException {
		logger.debug("SMTP SEND: " + cmd);
		cmd = cmd + CRLF;
		out.write(cmd.getBytes());
		out.flush();
	}

	String rcvStateMachine() throws IOException, RSETException {
		return rcvStateMachine(false);
	}

	String rcvStateMachine(boolean reportError) throws IOException, RSETException {
		StringBuffer buf = new StringBuffer();
		boolean error = reportError;
		String msg;
		while (true) {
			msg = rcv().trim();
			msg = msg.toLowerCase();
			if (msg.startsWith("rcpt to:")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("data")) {
				send("354 Enter message, ending with '.' on a line by itself");
				msg = "";
				logInputs = false;
				while (true) {
					msg = rcv();
					if (".".equals(msg.trim()))
						break;
					buf.append(msg).append(CRLF);
				}
				logInputs = true;

				send("250 OK");
				continue;
			}
			if (msg.startsWith("helo")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("ehlo")) {
				send("502 ehlo not supported - use helo");
				continue;
			}
			if (msg.startsWith("mail from:")) {
				mailFrom = msg.substring(msg.indexOf(':') + 1);
				send("250 OK");
				continue;
			}
			if (msg.startsWith("rset")) {
				send("250 OK");
				throw new RSETException();
			}
			if (msg.startsWith("vrfy")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("noop")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("quit")) {
				send("221 " + domainname + " closing connection");
				//				if (error)
				//					return "";
				return buf.toString();
			}
			send("503 bad sequence of commands - received " + msg);
			error = true;
		}
	}

	String rcv(String expect) throws IOException, RSETException {
		String msg = rcv().trim().toLowerCase();
		expect = expect.toLowerCase();
		if (expect != null && !msg.startsWith(expect)) {
			send("503 bad sequence of commands");
			return rcvStateMachine(true);
		}
		return msg;
	}

	String rcv() throws IOException, RSETException {
		String msg = in.readLine();
		if (logInputs)
			logger.debug("SMTP RCV: " + msg);
		return (msg == null) ? "" : msg;
	}

	String ccdaValidationType(TkPropsServer tps, String toAddr) {
		TkPropsServer props = tps.withPrefixRemoved("ccdatype");
		for (int i=1; i<50; i++) {
			String key = "directTo" + String.valueOf(i);
			String to = props.get(key, null);
			if (to == null)
				return null;
			if (toAddr.equals(to)) {
				key = "type" + String.valueOf(i);
				return props.get(key, null);
			}
		}
		return null;
	}

	String simpleEmailAddr(String addr) throws DirectParseException {
		addr = addr.trim();
		int fromi = addr.indexOf('@');
		if (fromi == -1)
			throw new DirectParseException("Cannot parse Direct Address " + addr);
		int toi = fromi;
		for (int i=fromi; i>=0; i--) {
			if (i ==0) {
				fromi = 0;
				break;
			}
			char c = addr.charAt(i);
			if (c =='"' || c=='<' || c == ' ' || c == '\t' || c == ':') {
				fromi = i + 1;
				break;
			}
		}
		for (int i=toi; i<addr.length(); i++) {
			char c = addr.charAt(i);
			if (c == '"' || c=='>') {
				String sim = addr.substring(fromi, i);
				return sim.trim();
			}
			if (i == addr.length() - 1) {
				return addr.substring(fromi).trim();
			}
		}
		throw new DirectParseException("Cannot parse Direct Address " + addr);
	}

	String header(List<String> headers, String name) {
		String prefix = (name + ":").toLowerCase();
		for (String h : headers) {
			String hl = h.toLowerCase();
			if (hl.startsWith(prefix))
				return h;
		}
		return "";
	}

	List<String> headers(StringBuffer b) {
		List<String> headers = new ArrayList<String>();
		String CR = "\n";

		int start = 0;
		int end = b.indexOf(CR);
		int length = end - start;

		while (end != -1) {
			if (length > 0) { 
				String header = b.substring(start, end).trim();
				if (header.length() == 0)
					break;
				logger.info("Header (" + header.length() + ") : " + header);
				headers.add(header);
				start = end;
				end = b.indexOf(CR, start + 1);
			}
		}

		logger.info("Found " + headers.size() + " headers");

		return headers;
	}
}
