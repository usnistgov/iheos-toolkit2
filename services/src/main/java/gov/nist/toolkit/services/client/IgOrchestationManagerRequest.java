package gov.nist.toolkit.services.client;

import java.io.Serializable;

/**
 *
 */
public class IgOrchestationManagerRequest implements Serializable {
    String userName;
    String environmentName;
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

    public boolean isIncludeLinkedIG() {
        return includeLinkedIG;
    }

    public void setIncludeLinkedIG(boolean includeLinkedIG) {
        this.includeLinkedIG = includeLinkedIG;
    }
}
