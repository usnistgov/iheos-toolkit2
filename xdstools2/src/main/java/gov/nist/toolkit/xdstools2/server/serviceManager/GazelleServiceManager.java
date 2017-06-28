package gov.nist.toolkit.xdstools2.server.serviceManager;

import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.CSVParser;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleConfigs;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.GazelleEntryFactory;
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.GenerateSingleSystem;
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.GenerateSystemShell;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;

public class GazelleServiceManager extends CommonService {

    static Logger logger = Logger.getLogger(GazelleServiceManager.class);

    Session session;
    String gazelleUrl;
    File actorsDir;
    File externalCacheFile;
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
            gazelleUrl = "http://10.242.100.100/gazelle-na/systemConfigurations.seam?testingSessionId=39&configurationType=WebServiceConfiguration";
            actorsDir = new File("/Users/bmajur/tmp/toolkit2/actors");
            externalCacheFile = new File("/Users/bmajur/tmp/toolkit2");
        } else {
            gazelleUrl = Installation.instance().propertyServiceManager().getPropertyManager().getToolkitGazelleConfigURL();
            actorsDir = new File(Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "actors");
            externalCacheFile = new File(Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache());
        }

        System.out.println("Pull config from " + gazelleUrl);
    }

    /**
     *
     * @param systemName system name possibly with - xxx extension
     * @return
     * @throws Exception
     */
    public String reloadSystemFromGazelle(String systemName) throws Exception {
        try {
            String id = (session == null) ? "42" : session.id();
            logger.debug(id + ": " + "reloadSystemFromGazelle(" + systemName + ")");
            String conflicts;

            if (!initDone) {
                initDone = true;
                init();   // loads gazelleUrl and actorsDir
            }

            if (gazelleUrl == null || gazelleUrl.equals(""))
                throw new Exception("Linkage to Gazelle not configured");

            GenerateSystemShell gazelleShell = new GenerateSystemShell(actorsDir, gazelleUrl);
            String log = "";
            if (systemName.equals("ALL")) {
                Collection<String> systemNames = gazelleShell.run();
                StringBuilder buf = new StringBuilder();
                for (String sname : systemNames) {
                    try {
                        buf.append(gazelleShell.getLogContents(sname));
                    } catch (Exception e) {
                        buf.append("No log for system " + sname + "\n");
                    }
                }
                log = buf.toString();
            }
            else {
                String realSystemName = GenerateSingleSystem.withoutExtension(systemName);
                gazelleShell.run(realSystemName);
                log = gazelleShell.getLogContents(realSystemName);
            }

            // force reload of all actor definitions
            if (!unitTest) {
                SiteServiceManager.getSiteServiceManager().reloadCommonSites();
            }
            if (unitTest)
                return null;
            return "<pre>\n" + log + "\n</pre>";
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception("Load failed.", e);
        }

    }

//    public String reloadSystemFromGazelle(String systemName) throws Exception {
//        String id = (session == null) ? "42" : session.id();
//        logger.debug(id + ": " + "reloadSystemFromGazelle(" + systemName + ")");
//        String conflicts;
//
//        if (!initDone) {
//            initDone = true;
//            init();
//        }
//
//        if (gazelleUrl == null || gazelleUrl.equals(""))
//            throw new Exception("Linkage to Gazelle not configured");
//
//
//        GazelleConfigs gConfigs = null;
//        OidConfigs oConfigs = null;
//
//        oConfigs = new OidConfigs();
//        new CSVParser(new OidEntryFactory()).parse(oConfigs, Io.stringFromFile(new File(actorsDir + File.separator + "ListOID.csv")));
//
//
//        if (systemName.equals("ALL")) {
//            new ConfigPull(gazelleUrl, actorsDir).pull();
//
//            gConfigs = new GazelleConfigs();
//            new CSVParser(new GazelleEntryFactory()).parse(gConfigs, Io.stringFromFile(new File(actorsDir + File.separator + "all.csv")));
//
//            conflicts = new ConfigToXml(gConfigs, oConfigs, actorsDir).process();
//        }
//        else {
//            new ConfigPull(gazelleUrl, actorsDir).pull(systemName);
//
//            gConfigs = new GazelleConfigs();
//            new CSVParser(new GazelleEntryFactory()).parse(gConfigs, Io.stringFromFile(new File(actorsDir + File.separator + systemName + ".csv")));
//
//            conflicts = new ConfigToXml(gConfigs, oConfigs, actorsDir).process();
//        }
//
//        System.err.println("Conflicts:\n" + conflicts);
//
//        // build Gazelle proxy configurations
//        gConfigs = loadAllGConfigs();
//        System.out.println("Load all - found " + gConfigs.size() + " configurations");
//        Properties props = ProxyConfigToCSV.buildProperties(gConfigs);
//        File outfile = new File(externalCacheFile, "proxy-ports.properties");
//        System.out.println("Saving " + outfile);
//        props.store(new FileWriter(outfile), "");
//
//        // force reload of all actor definitions
//        if (!unitTest) {
//            SiteServiceManager.getSiteServiceManager().reloadCommonSites();
//        }
//        if (unitTest)
//            return null;
//        return "<pre>\n" + conflicts + "\n</pre>";
//
//    }

    // load csv into gConfigs
    void parseGConfigs(String csv, GazelleConfigs gConfigs) {
        new CSVParser(new GazelleEntryFactory()).parse(gConfigs, csv);
    }

    GazelleConfigs loadAllGConfigs() {
        GazelleConfigs gConfigs = new GazelleConfigs();

        try {
            parseGConfigs(Io.stringFromFile(new File(actorsDir, "all.csv")), gConfigs);
            System.out.println("Found " + gConfigs.size() + " configs in " + new File(actorsDir, "all.csv"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse all.csv", e);
        }
        File[] files = actorsDir.listFiles();
        for (int i=0; i<files.length; i++) {
            File file = files[i];
            if (!file.getName().endsWith("csv")) continue;
            if (file.getName().endsWith("all.csv")) continue;
            try {
                parseGConfigs(Io.stringFromFile(file), gConfigs);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse all.csx", e);
            }
        }
        System.out.println("Found " + gConfigs.size() + " configs total ");
        return gConfigs;
    }

    public static void main(String[] args) {
        try {
            GazelleServiceManager gsm = new GazelleServiceManager();
//            gsm.reloadSystemFromGazelle("ALL");
            gsm.reloadSystemFromGazelle("OTHER_NIST_RED_2015");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
