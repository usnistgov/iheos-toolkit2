package gov.nist.toolkit.installation;


import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Installation {
    File warHome = null;
    File externalCache = null;
    String sep = File.separator;
    public TkProps tkProps = new TkProps();

    public final static String DEFAULT_ENVIRONMENT_NAME = "default";

    PropertyServiceManager propertyServiceMgr = null;
    static Logger logger = Logger.getLogger(Installation.class);

    static Installation me = new Installation();

    public String toString() {
        return String.format("warHome=%s externalCache=%s", warHome, externalCache);
    }

    static {
        // This works for unit tests if warhome.txt is installed as part of a unit test environment
        String warhomeTxt = null;
        try {
            warhomeTxt = installation().getClass().getResource("/warhome/warhome.txt").getFile();
        } catch (Throwable t) {}
        if (warhomeTxt != null) {
            installation().warHome(new File(warhomeTxt).getParentFile());
        }
//        String warTxt = null;
//        try {
//            warTxt = installation().getClass().getResource("/war/war.txt").getFile();
//        } catch (Throwable t) {}
//        if (warTxt != null) {
//            installation().warHome(new File(warTxt).getParentFile());
//        }
    }

    static public Installation installation() {
        return me;
    }

    static public Installation installation(ServletContext servletContext) {
        if (me.warHome == null)
            me.warHome(new File(servletContext.getRealPath("/")));
        return me;
    }

    private Installation() {
        logger.info(String.format("Installation rooted at %s", toString()));
    }

    public File warHome() {
        if (warHome == null) {
            String warTxt = null;
            try {
                warTxt = installation().getClass().getResource("/war/war.txt").getFile();
            } catch (Throwable t) {}
            if (warTxt != null) {
                installation().warHome(new File(warTxt).getParentFile());
            }
        }
        return warHome;
    }
    synchronized public void warHome(File warHome) {
        if (this.warHome != null /* && warHome().equals(warHome) */) {
            logger.info("... oops - warHome already initialized to " + warHome);
            return; /* already set */
        }
        logger.info("V2 - Installation - war home set to " + warHome);
        if (warHome == null)
            logger.error(ExceptionUtil.here("warhome is null"));
        this.warHome = warHome;
        propertyServiceMgr = null;
        propertyServiceManager();  // initialize
        if (externalCache == null) // this can be different in a unit test situation
            externalCache = new File(propertyServiceManager().getPropertyManager().getExternalCache());
        logger.info("Toolkit running at " + propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getToolkitPort());
    }

    public File externalCache() { return externalCache; }
    protected void externalCache(File externalCache) {
        this.externalCache = externalCache;
        logger.info("V2 Installation: External Cache set to " + externalCache);
        try {
            tkProps = TkLoader.tkProps(installation().getTkPropsFile()); //TkLoader.tkProps(new File(Installation.installation().externalCache() + File.separator + "tk_props.txt"));
        } catch (Exception e) {
//			logger.warn("Cannot load tk_props.txt file from External Cache");
            tkProps = new TkProps();
        }

    }

    public void overrideToolkitPort(String port) {
        propertyServiceManager().setOverrideToolkitPort(port);
    }

    public File getTkPropsFile() {
        return new File(Installation.installation().externalCache() + File.separator + "tk_props.txt");
    }



    public boolean initialized() { return warHome != null && externalCache != null; }

    public PropertyServiceManager propertyServiceManager() {
        if (propertyServiceMgr == null)
            propertyServiceMgr = new PropertyServiceManager();
        return propertyServiceMgr;
    }

    public File getActorsDirName() {
        File f = new File(externalCache() + File.separator + "actors");
        f.mkdirs();
        return f;
    }

    public File simDbFile() {
        return new File(externalCache(), "simdb");
    }

    public List<String> getListenerPortRange() {
        return propertyServiceManager().getListenerPortRange();
    }

    public File toolkitxFile() {
        return new File(warHome(), "toolkitx");
    }
    public File schemaFile() {
        return new File(toolkitxFile(), "schema");
    }
    public File testkitFile() {
        File testkit = propertyServiceManager().getTestkit();
        if (testkit != null) {
            logger.info(String.format("Testkit source is %s", testkit));
            return testkit;
        }
        testkit = new File(toolkitxFile(), "testkit");
        logger.info(String.format("Testkit source is %s", testkit));
        return testkit;
    }

    /**
     * This method return a list of testkit files sorted by priority level.
     * The first in the list is the top priority testkit.
     * It can contain a user testkit for an environment, an environment testkit (generated from code update)
     * and the toolkit default testkit.
     * It always contains at least the default testkit of the toolkit. The presence of the other
     * two depends on the existence.
     * @param environmentName name of the environment to look into for the environment specific testkits.
     * @param mesaSessionName name of the test session for the user specific testkit.
     * @return list of testkit files
     */
    public List<File> testkitFiles(String environmentName,String mesaSessionName) {
//		if (environmentName == null) environmentName=Installation.DEFAULT_ENVIRONMENT_NAME;
        List<File> testkits=new ArrayList<File>();
        if (environmentName!=null) {
            // paths to the testkit repository in the environment directory
            File environmentTestkitsFile = new File(environmentFile(environmentName), "testkits");
            File usrTestkit=null;
            if (mesaSessionName!=null) {
                // path to the user's testkit (based on the name of the test session)
                usrTestkit = new File(environmentTestkitsFile, mesaSessionName);
            }
            // path to the environment specific testkit (generated from Code Update)
            File environmentDefaultTestkit = new File(environmentTestkitsFile, "default");
            if (usrTestkit != null && usrTestkit.exists()) testkits.add(usrTestkit);
            if (environmentDefaultTestkit != null && environmentDefaultTestkit.exists())
                testkits.add(environmentDefaultTestkit);
        }
        // toolkit default testkit
        testkits.add(testkitFile());
        return testkits;
    }

    public String defaultEnvironmentName() { return propertyServiceManager().getDefaultEnvironment(); }

    public File environmentFile(String envName) {
        return new File(externalCache + sep + "environment" + sep + envName);
    }

    public File environmentFile() {
        return new File(externalCache + sep + "environment");
    }

    // Default codes.xml to use if no environments are configured
    public File internalEnvironmentFile(String envName) {
        return new File(new File(toolkitxFile(), "environment"), envName);
    }

    public File internalEnvironmentsFile() {
        return new File(toolkitxFile(), "environment");
    }

    public File sessionLogFile(String sessionId) {
        return new File(warHome + sep + "SessionCache" + sep + sessionId);
    }

    public File sessionCache() {
        return new File(warHome + sep + "SessionCache");
    }

    public File testLogCache() {
        return new File(externalCache + sep + "TestLogCache");
    }

    public static String defaultSessionName() { return "STANDALONE"; }
    public static String defaultServiceSessionName() { return "SERVICE"; }

    public File findTestkitFromTest(List<File> testkits, String id) {
        // FIXME during mvn build testkits is null
//        if (testkits!=null)
        for (File testkit:testkits){
            if (testkit!=null)
                if (testkit.exists()){
                    File[] areas=testkit.listFiles();
                    for (File area:areas){
                        File test=new File(area,id);
                        if (test.exists()){
                            return testkit;
                        }
                    }
                }
        }
        return null;
    }
}
