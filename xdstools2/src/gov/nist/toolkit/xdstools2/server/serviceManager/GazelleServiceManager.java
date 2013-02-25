package gov.nist.toolkit.xdstools2.server.serviceManager;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.CSVParser;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.ConfigPull;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.ConfigToXml;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleConfigs;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleEntryFactory;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.OidConfigs;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.OidEntryFactory;

import java.io.File;

import org.apache.log4j.Logger;

public class GazelleServiceManager extends CommonServiceManager {
	
	static Logger logger = Logger.getLogger(GazelleServiceManager.class);

	Session session;
	String gazelleUrl;
	File actorsDir;
	boolean unitTest = false;
	boolean initDone = false;
	
	public GazelleServiceManager(Session session) throws XdsException {
		this.session = session;
	}
	
	// Unit testing only
	GazelleServiceManager() {
		unitTest = true;
	}
	
	// Execution of this delayed. Not everything is initialized when constructor is called
	void init() {
		if (unitTest) {
			gazelleUrl = "http://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=21";
			actorsDir = new File("/Users/bill/tmp/toolkit/actors");
		} else {
			gazelleUrl = Installation.installation().propertyServiceManager().getPropertyManager().getToolkitGazelleConfigURL();
			actorsDir = new File(Installation.installation().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "actors");
		}
	}

	public String reloadSystemFromGazelle(String systemName) throws Exception {
		logger.debug(session.id() + ": " + "reloadSystemFromGazelle(" + systemName + ")");
		String conflicts;
		
		if (!initDone) {
			initDone = true;
			init();
		}

//		try {
			if (gazelleUrl == null || gazelleUrl.equals(""))
				throw new Exception("Linkage to Gazelle not configured");


			GazelleConfigs gConfigs = null; 
			OidConfigs oConfigs = null;

			oConfigs = new OidConfigs();
			new CSVParser(new File(actorsDir + File.separator + "oidSummary.csv"), oConfigs, new OidEntryFactory());


			if (systemName.equals("ALL")) {
				new ConfigPull(gazelleUrl, actorsDir).pull();

				gConfigs = new GazelleConfigs();
				new CSVParser(new File(actorsDir + File.separator + "all.csv"), gConfigs, new GazelleEntryFactory());

				conflicts = new ConfigToXml(gConfigs, oConfigs, actorsDir).run();
			}
			else {
				new ConfigPull(gazelleUrl, actorsDir).pull(systemName);

				gConfigs = new GazelleConfigs();
				new CSVParser(new File(actorsDir + File.separator + systemName + ".csv"), gConfigs, new GazelleEntryFactory());

				conflicts = new ConfigToXml(gConfigs, oConfigs, actorsDir).run();
			}

			System.err.println("Conflicts:\n" + conflicts);

			// force reload of all actor definitions
			if (!unitTest) {
				SiteServiceManager.getSiteServiceManager().reloadCommonSites();
			}
//		} catch (Exception e) {
//			logger.error(ExceptionUtil.exception_details(e));
//			throw new Exception("Call failed: " + e.getMessage());
//		}
			if (unitTest)
				return null;
		return "<pre>\n" + conflicts + "\n</pre>";

	}
	
	public static void main(String[] args) {
		try {
			new GazelleServiceManager().reloadSystemFromGazelle("ALL");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
