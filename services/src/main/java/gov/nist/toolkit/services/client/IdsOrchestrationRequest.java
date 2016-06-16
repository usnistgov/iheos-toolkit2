package gov.nist.toolkit.services.client;

import java.io.Serializable;

/**
 *
 */
public class IdsOrchestrationRequest implements Serializable {
    String userName;
    String environmentName;

    public IdsOrchestrationRequest() {}

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

}
