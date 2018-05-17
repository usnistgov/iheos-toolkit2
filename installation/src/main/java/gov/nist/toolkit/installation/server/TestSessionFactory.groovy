package gov.nist.toolkit.installation.server

import gov.nist.toolkit.installation.shared.ExpirationPolicy
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.utilities.id.UuidAllocator
import gov.nist.toolkit.utilities.io.Io
import groovy.transform.TypeChecked

@TypeChecked
class TestSessionFactory {

    // This does not test for prior existance - better to use TestSessionServiceManager
    static TestSession create() {
        return new TestSession(nonce())
    }

    static String nonce() {
        int size = Installation.instance().propertyServiceManager().nonceSize;
        if (size > 12) size = 12
        String base = UuidAllocator.allocate().reverse()
        String value = base.substring(0, size)
        return value.replaceAll('-', 'p')
    }

    // This does not test for prior existance - better to use TestSessionServiceManager
    static TestSession build() {
        TestSession testSession = create()
        initialize(testSession)
        return testSession
    }

    static void initialize(TestSession testSession) {
        Installation.instance().simDbFile(testSession).mkdirs()
        File testLogFile = Installation.instance().testLogCache(testSession)
        testLogFile.mkdirs()
        Installation.instance().actorsDir(testSession).mkdirs()
        Installation.instance().testSessionMgmtDir(testSession).mkdirs()
        // set default properties for any test sessions that are not
        // so initialized
        for (String testSessionName : getNames()) {
            TestSession testSession2 = new TestSession(testSessionName)
            Installation.instance().testSessionMgmtDir(testSession2).mkdirs()
            getPolicyProperties(testSession2)  // installs default if not present
        }
    }

    static List<String> getNames()  {
        (inSimDb() + inActors() + inTestLogs() + inMgmt()) as List
    }

    static Set<String> inSimDb() {
        Installation.instance().simDbFile().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    static Set<String> inActors() {
        Installation.instance().actorsDir().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    static Set<String> inTestLogs() {
        Installation.instance().testLogCache().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    static Set<String> inMgmt() {
        Installation.instance().testSessionMgmtDir().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    static final String TIMESTAMEFILE = 'timestamp'

    static void updateTimestanp(TestSession testSession) {
        Io.stringToFile(
                new File(Installation.instance().testSessionMgmtDir(testSession), TIMESTAMEFILE)
                , 'X')
    }

    static long sinceModification(TestSession testSession) {
        long lastModified = new File(Installation.instance().testSessionMgmtDir(testSession), TIMESTAMEFILE).lastModified()
        System.currentTimeMillis() - lastModified
    }

    static long _1_DAY_IN_MILLI =  24 * 60 * 60 * 1000
    static long _30_DAYS_IN_MILLI = 30 * _1_DAY_IN_MILLI

    static boolean isExpired(TestSession testSession) {
        ExpirationPolicy ePolicy = getExpirationPolicy(testSession)
        if (ePolicy == ExpirationPolicy.NO_ACTIVITY_FOR_30_DAYS) {
            return sinceModification(testSession) > _30_DAYS_IN_MILLI
        }
        false
    }

    static String expiresDescription(TestSession testSession) {
        ExpirationPolicy ePolicy = getExpirationPolicy(testSession)
        if (ePolicy == ExpirationPolicy.NO_ACTIVITY_FOR_30_DAYS) {
            long modification = sinceModification(testSession)
            int idays = modification / _1_DAY_IN_MILLI as Integer
            idays = 30 - idays
            String.valueOf(idays) + " Days"
        } else {
            "Never"
        }
    }

    static Date lastUpdated(TestSession testSession) {
        long lastModified = new File(Installation.instance().testSessionMgmtDir(testSession), TIMESTAMEFILE).lastModified()
        new Date(lastModified)
    }

    /*
      Test Session Policies
     */

    static private final String ExpirationPolicyLabel = 'ExpirationPolicy'

    static private Properties defaultProperties = new Properties()
    static private Properties nonDefaultProperties = new Properties()
    static {
        defaultProperties.setProperty(ExpirationPolicyLabel, ExpirationPolicy.NEVER.toString())
        nonDefaultProperties.setProperty(ExpirationPolicyLabel, ExpirationPolicy.NO_ACTIVITY_FOR_30_DAYS.toString())
    }

    static File policyPropertiesFile(TestSession testSession) {
        new File(Installation.instance().testSessionMgmtDir(testSession), "policy.properties")
    }

    static void setExpirationPolicy(TestSession testSession, ExpirationPolicy expirationPolicy) {
        Properties properties = getPolicyProperties(testSession)
        properties.setProperty(ExpirationPolicyLabel, expirationPolicy.toString())
        savePolicyProperties(testSession, properties)
    }

    static ExpirationPolicy getExpirationPolicy(TestSession testSession) {
        Properties properties = getPolicyProperties(testSession)
        ExpirationPolicy.valueOf(ExpirationPolicy, properties.getProperty(ExpirationPolicyLabel))
    }

    private static Properties getPolicyProperties(TestSession testSession) {
        Properties properties = new Properties()
        try {
            properties.load(new FileInputStream(policyPropertiesFile(testSession)))
        } catch (IOException e) {
            savePolicyProperties(testSession, (testSession == TestSession.DEFAULT_TEST_SESSION) ? defaultProperties : nonDefaultProperties)
            properties.load(new FileInputStream(policyPropertiesFile(testSession)))
        }
        properties
    }

    private static void savePolicyProperties(TestSession testSession, Properties properties) {
        properties.store(new FileOutputStream(policyPropertiesFile(testSession)), '')
    }


    /*
    static createMarkerFile(File parent) {
        try {
            File userModeMarkerFile = getUserModeMarkerFile(parent)
            Io.stringToFile(userModeMarkerFile, "")
        } catch (Exception ex) {}
    }

    static File getUserModeMarkerFile(File parent) throws Exception {
       if (Installation.instance().propertyServiceManager().isMultiuserMode()) {
          return new File(parent, Installation.instance().propertyServiceManager().propertyManager.MULTIUSER_MODE + ".txt")
       } else if (Installation.instance().propertyServiceManager().isCasMode()) {
           return new File(parent, Installation.instance().propertyServiceManager().propertyManager.CAS_MODE + ".txt")
       }
        throw new Exception("Current user mode not supported!")
    }
    */
}
