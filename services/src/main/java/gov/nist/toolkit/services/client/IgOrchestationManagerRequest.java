package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.Pid;

import java.io.Serializable;

/**
 *
 */
public class IgOrchestationManagerRequest implements Serializable {
    String userName;
    String environmentName;
    Pid patientId;
    boolean includeLinkedIG;

    public IgOrchestationManagerRequest() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public Pid getPatientId() {
        return patientId;
    }

    public void setPatientId(Pid patientId) {
        this.patientId = patientId;
    }

    public boolean isIncludeLinkedIG() {
        return includeLinkedIG;
    }

    public void setIncludeLinkedIG(boolean includeLinkedIG) {
        this.includeLinkedIG = includeLinkedIG;
    }
}
