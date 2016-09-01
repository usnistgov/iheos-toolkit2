package gov.nist.toolkit.services.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 */
public class IdsOrchestrationRequest implements Serializable, IsSerializable {
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
