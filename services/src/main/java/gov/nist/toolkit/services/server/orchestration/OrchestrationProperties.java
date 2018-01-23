package gov.nist.toolkit.services.server.orchestration;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.session.server.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * Manage a Properties file for the Orchestration process.  This is where the process state
 * is maintained. In general there are two types of properties:  Patient Ids which get special automation
 * because we can; and all other properties.
 */
public class OrchestrationProperties {
    private File orchestrationPropFile;
    private Properties orchProps = new Properties();
    private boolean updated = false;
    private boolean pidsAllocated = false;
    private Collection<String> pidPropNames;
    private Session session;
    private boolean forceNewPatientIds;

    public OrchestrationProperties(Session session, TestSession testSession, ActorType actorType, Collection<String> pidPropNames, boolean forceNewPatientIds) throws Exception {
        this.session = session;
        this.pidPropNames = pidPropNames;
        this.forceNewPatientIds = forceNewPatientIds;
        orchestrationPropFile = Installation.instance().orchestrationPropertiesFile(testSession, actorType.getShortName());
        if (orchestrationPropFile.exists())
            orchProps.load(new FileInputStream(orchestrationPropFile));
        allocatePids();
    }

    private void allocatePids() throws Exception {
        for (String pidName : pidPropNames) {
            String value = orchProps.getProperty(pidName);
            if (forceNewPatientIds || value == null || value.equals("") ) {
                setProperty(pidName, session.allocateNewPid().asString());
                updated = true;
                pidsAllocated = true;
            }
        }
    }

    public void clear() {
        orchProps.clear();
    }

    public String getProperty(String name) {
        return orchProps.getProperty(name);
    }

    public void setProperty(String name, String value) {
        orchProps.setProperty(name, value);
        updated = true;
    }

    public boolean updated() {
        return updated;
    }

    public boolean isPidsAllocated() {
        return pidsAllocated;
    }

    public void save() throws IOException {
        if (updated) {
            orchestrationPropFile.getParentFile().mkdirs();
            orchProps.store(new FileOutputStream(orchestrationPropFile), null);
            updated = false;
        }

    }

}
