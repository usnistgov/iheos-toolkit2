package gov.nist.toolkit.xdstools2.server.api;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.TransactionSettings;
import gov.nist.toolkit.testengine.Xdstest2;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bill on 6/15/15.
 */
public class ClientApi implements SecurityParams {
    Session session;
    File testkitFile;

    public ClientApi(Session session) {
        this.session = session;
        this.testkitFile = Installation.installation().testkitFile();
    }

    public boolean run(String testname, Site site, boolean tls, Map<String, String> parms) throws Exception {
        Xdstest2 engine = new Xdstest2(Installation.installation().toolkitxFile(), this);
        engine.setTestkitLocation(testkitFile);
        engine.setTest(testname);
        engine.setSite(site);
        TransactionSettings ts = new TransactionSettings();
        ts.writeLogs = true;
        ts.patientId = parms.get("$patientid$");
        return engine.run(parms, null, true, ts);
    }






    // Start - Things required by SecurityParams parent class
    @Override
    public File getCodesFile() throws EnvironmentNotSelectedException {
        return null;
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
