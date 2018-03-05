package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.io.Serializable;

/**
 *
 */
abstract public class AbstractOrchestrationRequest implements Serializable, IsSerializable  {
    private TestSession testSession;
    private String environmentName;
    private PifType pifType;
    private SiteSpec registrySut;
    private boolean useExistingState = true;   // useExistingState == !reset
    private boolean selfTest = false;
    private ActorOption actorOption = new ActorOption();

    public TestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(TestSession testSession) {
        this.testSession = testSession;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public PifType getPifType() {
        return pifType;
    }

    public void setPifType(PifType pifType) {
        this.pifType = pifType;
    }

    public SiteSpec getRegistrySut() {
        return registrySut;
    }

    public void setRegistrySut(SiteSpec registrySut) {
        this.registrySut = registrySut;
    }

    public boolean isUseExistingState() {
        return useExistingState;
    }

    public void setUseExistingState(boolean useExistingState) {
        this.useExistingState = useExistingState;
    }

    public void selfTest(boolean value) { this.selfTest = value; }

    public boolean selfTest() { return this.selfTest; }

    public ActorOption getActorOption() {
        return actorOption;
    }

    public void setActorOption(ActorOption actorOption) {
        this.actorOption = actorOption;
    }
}
