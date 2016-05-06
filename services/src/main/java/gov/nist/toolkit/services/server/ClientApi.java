package gov.nist.toolkit.services.server;

import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.engine.TransactionSettings;
import gov.nist.toolkit.testengine.engine.Xdstest2;
import gov.nist.toolkit.testengine.transactions.CallType;
import gov.nist.toolkit.testengine.transactions.TransactionTransportFactory;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class ClientApi implements SecurityParams {
    Session session;
    File testkitFile;
    static Logger logger = Logger.getLogger(ClientApi.class);

    public ClientApi() {
        this(UnitTestEnvironmentManager.setupLocalToolkit());
    }

    public ClientApi(Session session) {
        this.session = session;
        // TODO Change for list of files (testkitFiles())?
        this.testkitFile = Installation.installation().testkitFile();
    }

    public Session getSession() { return session; }

    public boolean runTest(TestInstance testInstance, Site site, boolean tls, Map<String, String> parms, boolean stopOnFirstError, CallType callType) throws Exception {
        Xdstest2 engine = new Xdstest2(Installation.installation().toolkitxFile(), this);
        engine.setTestkitLocation(testkitFile);
        engine.addTest(testInstance);
        engine.setSite(site);
        TransactionSettings ts = new TransactionSettings();
        ts.transactionTransport = TransactionTransportFactory.get(callType);
        ts.writeLogs = true;
        ts.patientId = parms.get("$patientid$");
        ts.securityParams = this;
        ts.logRepository =
                LogRepositoryFactory.getRepository(
                        Installation.installation().testLogCache(),
                        session.getId(),
                        LogIdIOFormat.JAVA_SERIALIZATION,
                        LogIdType.SPECIFIC_ID,
                        testInstance);
        System.out.println("RUN TEST " + testInstance + " to log " + ts.logRepository);
        logger.info("TransactionSettings: " + ts);
        return engine.run(parms, null, stopOnFirstError, ts);
    }

    public boolean runTestCollection(String testCollectionName, Site site, boolean tls, Map<String, String> parms, boolean stopOnFirstError, CallType callType) throws Exception {
        Xdstest2 engine = new Xdstest2(Installation.installation().toolkitxFile(), this);
        engine.setTestkitLocation(testkitFile);
        engine.addTestCollection(testCollectionName);
        engine.setSite(site);
        TransactionSettings ts = new TransactionSettings();
        ts.transactionTransport = TransactionTransportFactory.get(callType);
        ts.writeLogs = true;
        ts.patientId = parms.get("$patientid$");
        ts.securityParams = this;
        //  - this writes to the directory null instead of updating it given the current test
        ts.logRepository =
                LogRepositoryFactory.getRepository(
                        Installation.installation().testLogCache(),
                        session.getId(),
                        LogIdIOFormat.JAVA_SERIALIZATION,
                        LogIdType.SPECIFIC_ID,
                        null);
        System.out.println("RUN TEST COLLECTION " + testCollectionName + " to log " + ts.logRepository);
        return engine.run(parms, null, stopOnFirstError, ts);
    }

    // Start - Things required by SecurityParams parent class
    @Override
    public File getCodesFile() throws EnvironmentNotSelectedException {
        return EnvSetting.getEnvSetting(session.getId()).getCodesFile();
    }

    @Override
    public File getKeystore() throws EnvironmentNotSelectedException {
        return null;
    }

    @Override
    public String getKeystorePassword() throws IOException, EnvironmentNotSelectedException {
        return null;
    }

    @Override
    public File getKeystoreDir() throws EnvironmentNotSelectedException {
        return null;
    }
    // End - Things required by SecurityParams parent class

}
