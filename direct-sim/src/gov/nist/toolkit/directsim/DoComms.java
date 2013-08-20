package gov.nist.toolkit.directsim;

import gov.nist.messageDispatch.MessageDispatchUtils;
import gov.nist.toolkit.actorfactory.DirectActorFactory;
import gov.nist.toolkit.directsupport.SMTPException;
import gov.nist.toolkit.email.Emailer;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simulators.support.ValidateMessageService;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.TkPropsServer;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.utilities.io.Io;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
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

public class DoComms implements Runnable {
	Socket server;
	File externalCache;
	String pathToPrivateKey;
	String directFrom = null;    // Direct from address reported in SMTP protocol
	List<String> directTo = new ArrayList<String>();
	boolean logInputs = true;
	TkPropsServer reportingProps = null;
	StringBuffer message = new StringBuffer();
	String contactAddr = null;

	static Logger logger = Logger.getLogger(DoComms.class);

	DoComms(Socket server, File externalCache, String pathToPrivateKey) {
		this.server=server;
		this.externalCache = externalCache;
		this.pathToPrivateKey = pathToPrivateKey;
	}
	
	/**
	 * For unit testing only
	 */
	public DoComms() {  }  
	/**
	 * For unit testing only
	 */
	public DoComms(File externalCache) {  
		this.externalCache = externalCache;
	}

	public void run () {

		if (readSMTPMessage() == false)
			return;
		logger.info("Processing message from " + directFrom);

		MimeMessage mmsg;
		try {
			mmsg = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null), Io.bytesToInputStream(message.toString().getBytes()));
			MessageDispatchUtils.isMDN(mmsg);   // if this is going to fail spectacularly, let it do it early
		} catch (MessagingException e2) {
			logger.error("Message fails MimeMessage parser");
			return;
		}
		
		logger.info("Mime Message parsing successful");

		HtmlValFormatter hvf = new HtmlValFormatter();

//		List<String> headers = headers(message);

		// Valid Direct (From) addr?
		DirectUserManager dum = new DirectUserManager();
		if (!dum.directUserExists(directFrom)) {
			// looks like spam - unregistered direct (from) Addr
			logger.error("Throw away message from " + directFrom + " not a registered Direct (From) email account name");
			return;
		}

		contactAddr = getContactAddr(hvf, directFrom);
		if (contactAddr == null) {
			logger.error("No contact address listed for Direct (From) address " + directFrom + " - cannot return report - giving up");
			return;
		}

		logger.info("Direct addr (From) " + directFrom + " is registered and has contact email of " + contactAddr);

		/****************************************************************************************
		 * 
		 * All errors detected after this can be reported back to the user via their Contact Addr.
		 * 
		 ****************************************************************************************/

		StringBuffer validationReport = new StringBuffer();
		validationReport.append("<html><head><title>Validation Results</title></head><body>");
		validationReport.append("<h1>Validation Results</h1>");
		validationReport.append("<p>Valiation from " + new Date().toString() + "</p>");


		MessageValidationResults mvr = null;

		// Load only the relevant properties
		reportingProps = reportingProps.withPrefixRemoved("direct.reporting");

		logger.debug("reportingProperties:\n" + reportingProps.toString() + "\n");

		// Reporting enclosure - throw ReportException to flush messages and continue
		try {



			//			ContactRegistrationData crd = dum.contactRegistrationData(contactAddr);

			//			String to = null;
			//			try {
			//				to = new SMTPAddress().parseEmailAddr(toHeader);
			//			} catch (Throwable e) {
			//				logger.error("Cannot parse To Header:  " + ExceptionUtil.exception_details(e));
			//				hvf.addError("Cannot parse To Header:  " + e.getMessage());
			//				throw new ReportException();
			//			}
			//
			//			if (to.equals("")) {
			//				logger.error("To Addr empty or missing");
			//				hvf.addError("To Addr empty or missing");
			//				throw new ReportException();
			//			}

			//			byte[] privKey = crd.getCert(from);
			logger.info("Loading private key (decryption) from " + pathToPrivateKey);
			byte[] privKey = Io.bytesFromFile(new File(pathToPrivateKey));  // This will be used to un-envelope the part so the private key is needed

			if (privKey == null) {
				hvf.h2("Error");
				hvf.addError("Cannot load private decryption key");
				logger.error("Cannot load private decryption key");
			}


			TkPropsServer ccdaProps = reportingProps.withPrefixRemoved("ccdatype");
			if (directTo.size() > 1) {
				String msg = "Multiple TO addresses pulled from SMTP protocol headers - cannot determine which CCDA validator to run - CCDA validation will be skipped"; 
				logger.warn(msg);
				hvf.blue(msg);
			} else if (directTo.size() == 0) {
					String msg = "No TO addresses pulled from SMTP protocol headers - cannot determine which CCDA validator to run - CCDA validation will be skipped"; 
					logger.warn(msg);
					hvf.blue(msg);
			} 
//			else {
//				String to = directTo.get(0);
//				for (int i=1; i<500; i++) {
//					String en = Integer.toString(i);
//					String type = ccdaProps.get("type" + en, null);
//					String ccdaTo = ccdaProps.get("directTo" + en, null);
//					if (type == null || ccdaTo == null)
//						break;
//					if (ccdaTo.equals(to)) {
//						ccdaType = type;
//						break;
//					}
//				}
//			}
			
			// ccdaType tells us the document type to validate against

			// Validate
			// yadda, yadda, yadda
			byte[] messageBytes = message.toString().getBytes();
			GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
			ValidateMessageService vms = new ValidateMessageService(null, null);
//			String simpleToHeader = simpleEmailAddr(toHeader);
//			logger.debug("toHeader is " + toHeader);
//			logger.debug("    which reduces to " + simpleToHeader);

			ValidationContext vc = new ValidationContext();
			vc.isDIRECT = true;
			vc.updateable = true;
			vc.ccdaType = ccdaValidationType(reportingProps, getDirectTo());
			vc.privKey = privKey;
			vc.privKeyPassword = reportingProps.get("privKeyPassword", "");

			logger.info("To: " + getDirectTo() + " translates to ccda type of " + vc.ccdaType);

			logger.info("Message Validation Begins");
			
			mvr = vms.runValidation(vc, null, messageBytes, privKey, gerb);
			
			logger.info("Message Validation Complete");

		} 
//		catch (DirectParseException e) {
//			logger.error(ExceptionUtil.exception_details(e));
//			hvf.h2("Error");
//			hvf.addError("Error: " + e.getMessage());
//		}
//		catch (ReportException re) {
//			// nothing to actually do here, more of a goto than an error handling situation
//		} 
		catch (Exception e) {
			logger.error("Message Validation Error: " + ExceptionUtil.exception_details(e));
			hvf.h2("Error");
			hvf.addError("Error: " + e.getMessage());
		}

		MessageValidatorDisplay mvd = new MessageValidatorDisplay(hvf);
		if (mvr != null)
			mvd.displayResults(mvr);


		logger.info("Starting report generation");
		
		validationReport.append(hvf.toHtmlTemplate(mvr));

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

		
		logger.info("Send report");
		
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
	
	String getDirectTo() {
		if (directTo.size() == 0 || directTo.size() > 1)
			return null;
		return directTo.get(0);
	}

	public String getContactAddr(HtmlValFormatter hvf, String directFrom) {
		directFrom = stripBrackets(directFrom);
		String contactAddr;
		// Get Contact Addr
		DirectRegistrationManager drm = new DirectRegistrationManager(externalCache);
		DirectRegistrationDataServer drd = null;

		try {
			drd = drm.load(directFrom);
		} catch (FileNotFoundException fnf) {
			logger.warn("Direct (From) address not registered");
		} catch (Exception e) {
			hvf.h2("Error");
			hvf.addError("Internal error loading configuration for Direct address (From) " + directFrom +
					e.getMessage());
			logger.error("Internal error loading configuration for Direct address (From) " + directFrom +
					e.getMessage());
			return null;
		}
		contactAddr = drd.contactAddr;
		return contactAddr;
	}

	boolean readSMTPMessage() {
		InetAddress ia = server.getInetAddress();
		String stars = "**********************************************************";
		logger.info(stars + "\n" + "Connection from " + ia.getHostName() + " (" + ia.getHostAddress() + ")");

		File propFile = new File(externalCache + File.separator +  "tk_props.txt");
		logger.debug("Loading properties from " + propFile);
		try {
			reportingProps = TkLoader.LOAD(propFile);
			logger.debug("Properties are\n" + reportingProps.toString());
			Installation.installation().warHome(new File(getWarDir()));
		} 
		catch (IOException e1) {
			logger.error("Error loading properties", e1);
			return false;
		}
		catch (Exception e1) {
			logger.error("Error loading properties", e1);
			return false;
		}

		try {
			// smtpFrom set as a side-effect.  This is the from address from the SMTP protocol elements
			// replies should go here as well as this addr should be used to lookup contact addr.
			String m = readIncomingSMTPMessage(server, reportingProps.get("direct.toolkit.smtp.domain"));   //"smtp.hit-testing.nist.gov");
			message.append(m);
		} catch (EOFException e) {
			logger.warn("IOException on socket listen: " + e);
			return true;
		} catch (IOException ioe) {
			logger.error("IOException on socket listen: " + ioe);
			return false;
		} catch (SMTPException e) {
			logger.error("SMTPException on socket listen: " + e);
			return false;
		} catch (PropertyNotFoundException e) {
			logger.error("tk_props property direct.toolkit.smtp.domain not found: " + e);
			return false;
		} catch (Exception e) {
			logger.error("Protocol error on socket listen: " + e);
			return false;
		}
		finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		return true;
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

	String readIncomingSMTPMessage(Socket socket, String domainname) throws IOException, SMTPException, Exception {
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

	String rcvStateMachine() throws IOException, RSETException, Exception {
		return rcvStateMachine(false);
	}

	String rcvStateMachine(boolean reportError) throws IOException, RSETException, Exception {
		StringBuffer buf = new StringBuffer();
		String msg;
		while (true) {
			msg = rcv().trim();
			msg = msg.toLowerCase();
			if (msg.startsWith("rcpt to:")) {
				String to = msg.substring(msg.indexOf(':') + 1);
				if (to != null && !to.equals(""))
					directTo.add(stripBrackets(to));
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
				directFrom = stripBrackets(msg.substring(msg.indexOf(':') + 1));
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
			throw new Exception("503 bad sequence of commands - received " + msg);
		}
	}

	String rcv(String expect) throws IOException, RSETException, Exception {
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
		if (toAddr == null)
			return null;
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
	
	/**
	 * Strip surrounding <    >  brackets if present
	 * @param in
	 * @return
	 */
	public String stripBrackets(String in) {
		if (in == null || in.length() == 0)
			return in;
		in = in.trim();
		int openI = in.indexOf('<');
		while (openI > -1) {
			in = in.substring(1);
			openI = in.indexOf('<');
		}
		
		if (in.length() == 0)
			return in;
		
		int closeI = in.indexOf('>');
		if (closeI > 0) 
			in = in.substring(0, closeI);
		
		return in;
	}
	
	public String getWarDir() {
		try {
			String dir = reportingProps.get("direct.reporting.directory");
			String ttt = reportingProps.get("toolkit.servlet.context");
			String[] splitDir = dir.split(File.separator);
			String warDir = "";
			for(int i=0;i<splitDir.length-2;i++) {
				warDir += File.separator + splitDir[i];
			}
			warDir += File.separator + ttt;
			return warDir;
		} catch (PropertyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
		
	}

}