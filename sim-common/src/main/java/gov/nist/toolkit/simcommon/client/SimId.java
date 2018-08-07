package gov.nist.toolkit.simcommon.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.io.Serializable;

/**
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SimId implements Serializable, IsSerializable {

    private static final String SEPARATOR = "__";
    private static final String SLASH = "/";

    private TestSession testSession = null;
    private String id = null;
    private String actorType = null;
    private String environmentName = null;
    private boolean fhir = false;

    // server only
    public SimId(TestSession testSession, String id, String actorType, String environmentName, boolean fhir) throws BadSimIdException {
        this(testSession, id, actorType);
        this.environmentName = environmentName;
        this.fhir = fhir;
    }

    // server only
    public SimId(TestSession testSession, String id, String actorType, String environmentName) throws BadSimIdException {
        this(testSession, id, actorType);
        this.environmentName = environmentName;
    }

    // client only
    public SimId(TestSession testSession, String id, String actorType) throws BadSimIdException {
        this(testSession, id);
        this.actorType = actorType;
    }

    public SimId(SiteSpec siteSpec, TestSession testSession) {
        this(testSession, (siteSpec == null) ? null : siteSpec.getName());
        if (siteSpec != null && siteSpec.getActorType() != null)
            actorType = siteSpec.getTypeName();
    }

    // client and server
    public SimId(TestSession testSession, String id) throws BadSimIdException {
        if (testSession == null) throw new ToolkitRuntimeException("TestSession is null");

        build(testSession, id);
    }

    // server  ?????? why ???????
//    public SimId(String id) throws BadSimIdException {
//        if (id != null) {
//            if (id.contains(SEPARATOR)) {
//                String[] parts = id.split(SEPARATOR, 2);
//                build(new TestSession(parts[0]), parts[1]);
//            } else {
//                build(new TestSession(DEFAULT_USER), id);
//            }
//        }
//    }


    public SimId() {}

    public SimId forFhir() {
        fhir = true;
        return this;
    }

    public boolean isFhir() { return fhir; }

    private void build(TestSession testSession, String id) throws BadSimIdException {
        testSession.clean();
        id = cleanId(id);
        if (testSession.getValue().contains(SEPARATOR)) throw new BadSimIdException(SEPARATOR + " is illegal in simulator testSession name");
        if (testSession.getValue().contains(SLASH)) throw new BadSimIdException(SLASH + " is illegal in simulator testSession name");
        if (id.contains(SLASH)) throw new BadSimIdException(SLASH + " is illegal in simulator id");
        this.testSession = testSession;
        this.id = id;
    }

//    public boolean equals(SimId simId) {
//        return this.testSession.equals(simId.testSession) && this.id.equals(simId.id);
//    }

    // equals and hashCode ignore FHIR status on purpose


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimId simId = (SimId) o;

        if (testSession != null ? !testSession.equals(simId.testSession) : simId.testSession != null) return false;
        return id != null ? id.equals(simId.id) : simId.id == null;
    }

    @Override
    public int hashCode() {
        int result = testSession != null ? testSession.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public String toString() { return testSession + SEPARATOR + id; }

    public String validateState() {
        StringBuilder buf = new StringBuilder();

        if (testSession == null || testSession.equals("")) buf.append("No testSession specified\n");
        if (id == null || id.equals("")) buf.append("No id specified\n");
        if (actorType == null || actorType.equals("")) buf.append("No actorType specified\n");
        if (environmentName == null || environmentName.equals("")) buf.append("No environmentName specified");

        if (buf.length() == 0) return null;   // no errors
        return buf.toString();
    }

    String cleanId(String id) { return id.replaceAll("\\.", "_").toLowerCase(); }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public TestSession getTestSession() {
        return testSession;
    }

    public String getId() {
        return id;
    }

    public boolean isTestSession(TestSession testSession) {
        return testSession != null && testSession.equals(this.testSession);
    }
    public boolean isValid() { return (!isEmpty(testSession.getValue())) && (!isEmpty(id)); }
    public void setValid(boolean x) { }
    boolean isEmpty(String x) { return x == null || x.trim().equals(""); }

    public SiteSpec getSiteSpec() {
        SiteSpec siteSpec = new SiteSpec(testSession);
        siteSpec.setName(toString());
        if (actorType != null)
            siteSpec.setActorType(ActorType.findActor(actorType));
        return siteSpec;
    }

}
