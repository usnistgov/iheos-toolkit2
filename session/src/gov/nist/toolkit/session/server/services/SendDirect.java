package gov.nist.toolkit.session.server.services;

import gov.nist.direct.client.config.SigningCertType;
import gov.nist.direct.config.DirectConfigManager;
import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.common.coder.Base64Coder;
import gov.nist.toolkit.dns.DnsLookup;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.testengine.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xbill.DNS.TextParseException;

public class SendDirect extends CommonServiceManager {
	Session session;
	Map<String, String> params;
	byte[] signingCert;   // need password
	byte[] encryptionCert;
	String signingPassword;
	String password2;

	static final Logger logger = Logger.getLogger(SendDirect.class);

	/**
	 * Send a Direct message.  Encryption cert comes from one of 3 sources:
	 * 1) Uploaded via UI so it arrives here in the encryptionCert param
	 * 2) Configured in toolkit for the target domain
	 * 3) From DNS
	 * These three locations are checked in this order.
	 * @param session
	 * @param params
	 * @param signingCert
	 * @param encryptionCert
	 * @param signingCertPassword
	 * @param unused
	 */
	public SendDirect(Session session, Map<String, String> params, byte[] signingCert, byte[] encryptionCert, String signingCertPassword, String unused)  {
		this.session = session;
		this.params = params;
		this.signingCert = signingCert;
		this.encryptionCert = encryptionCert;
		this.signingPassword = signingCertPassword;
		this.password2 = unused;
	}

	public List<Result> run() {
		try {
			String user = "DefaultDirectUser";	
			String directToAddr = params.get("$direct_to_address$");
			List<String> sections = null;
			String encCertSource = "";

			String[] areas = new String[1];
			areas[0] = "utilities";

			Map<String, Object> params2 = new HashMap<String, Object>();

			//***********************************************
			//
			// Signing Cert (sign with my private key)
			//
			//***********************************************

			// GUI selects which signing cert to use
			String signingCertName = params.get("$signing_cert$");
			if (signingCertName == null || signingCertName.equals("")) 
				throw new Exception("No signing cert selected");
			SigningCertType signingCertType = SigningCertType.valueOf(signingCertName);
			logger.info("Signing cert type is " + signingCertType);
			DirectConfigManager directConfig = new DirectConfigManager(Installation.installation().externalCache());
			signingCert = directConfig.getSigningCert(signingCertType);
			signingPassword = directConfig.getSigningCertPassword(signingCertType);
			params2.put("signingCert", signingCert);
			params.put("signingCertPassword", signingPassword);

			//***********************************************
			//
			// Discover target Direct Server Name
			//
			//***********************************************

			String targetDomain = params.get("$direct_to_domain$").trim();
			logger.info("Sending Direct msg to " + directToAddr);
			logger.info("    target domain is " + targetDomain);

			if (targetDomain == null || targetDomain.equals("")) {
				logger.error("    target domain is empty, not provided by UI");
				throw new Exception("No target domain provided by UI");
			}

			String directServerName = getDirectServerName(targetDomain); 
			logger.info("    with mail server at " + directServerName);

			params.put("$direct_server_name$", directServerName);   // used in testplan

			//***********************************************
			//
			// Encryption Cert  (encrypt with target's public key)
			// Try in this order:
			//    UI, preconfigured, DNS
			//
			//***********************************************

			if (!isEmpty(encryptionCert)) {
				logger.info("    Encryption cert provided by UI");
				encCertSource = "User Interface";
			}

			if (isEmpty(encryptionCert) ) {
				// not uploaded - pre-installed for a known domain - go find it
				// it is required to be in .der format 
				File certFile = directConfig.getEncryptionCertFile(targetDomain);
				//				if (certFile == null)
				//					throw new Exception("Cannot load pre-installed cert for domain " + targetDomain);
				if (certFile != null)
					encryptionCert = Io.bytesFromFile(certFile);

				if (!isEmpty(encryptionCert)) {
					logger.info("    Encryption cert found installed in toolkit");
					encCertSource = "Installed in toolkit";
				}
			} 

			if (isEmpty(encryptionCert)) {
				// not uploaded or pre-installed for the target domain.  Try fetching
				// from DNS.
				DnsLookup dl = new DnsLookup();
				String encCertString = dl.getCertRecord(targetDomain);
				if (encCertString != null)
					encryptionCert = Base64Coder.decode(encCertString);
				if (!isEmpty(encryptionCert)) {
					logger.info("    Encryption cert pulled from DNS");
					encCertSource = "DNS";
				}

			}
			if (isEmpty(encryptionCert)) {
				logger.error("    Encryption cert for domain [" + targetDomain + "] not available from UI, toolkit configuration, or DNS");
				return asList(new Result().simpleError("Encryption cert for domain [" + targetDomain + "] not available from UI, toolkit configuration, or DNS"));
				//				throw new Exception("Encryption cert for domain <" + targetDomain + "> not available from UI, toolkit configuration, or DNS");
			}
			params2.put("encryptionCert", encryptionCert);


			escapeWindowsBackslashes(params);

			session.isSoap = false;

			//			session.transactionSettings.logDir = new File(
			//					Installation.installation().directSendLogFile("bill") + 
			//					File.separator + new SimDb().nowAsFilenameBase()  );

			session.transactionSettings.logRepository = //new DirectRepository().getNewLogRepository(user);
					new LogRepositoryFactory().getRepository(Installation.installation().directSendLogs(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
			session.transactionSettings.user = session.getMesaSessionName();

			Result r = session.xdsTestServiceManager().xdstest("DirectSendTemplate", sections, params, params2, areas, true);
			Result rSrc = new Result().simpleStatus("Source of encryption cert was " + encCertSource);
			return asList(rSrc, r);
		} catch (Throwable e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

	// this only applies to certificates in byte[] format
	boolean isEmpty(byte[] b) {
		if (b == null) return true;
		if (b.length < 10) return true;
		return false;
	}

	void escapeWindowsBackslashes(Map<String, String> parms) {
		for (String key : parms.keySet()) {
			String value = parms.get(key);
			if (value != null) {
				value = value.replaceAll("\\\\", "\\\\\\\\");
				parms.put(key, value);
			}
		}
	}

	static public void main(String[] args) {
		String value = "ab\\cd";
		String value2 = value.replaceAll("\\\\", "\\\\\\\\");
		System.out.println("new value is " + value2);
	}

	String getDirectServerName(String domainName) {
		String directServerName = null;
		try {
			directServerName = new DnsLookup().getMxRecord(domainName);
		} catch (TextParseException e) {
			logger.error("    Error parsing MX record from DNS - for domain " + domainName);
		}

		if (directServerName != null && !directServerName.equals(""))
			return directServerName;

		logger.error("    MX record lookup in DNS did not provide a mail handler hostname for domain " + domainName);
		directServerName = "smtp." + domainName;
		logger.error( "    Guessing at mail server name - " + directServerName);
		return directServerName;
	}
}
