package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class RepOrchestrationRequest implements Serializable, IsSerializable {
    private String userName;
    private String environmentName;
    private boolean useExistingSimulator = false;

    public RepOrchestrationRequest() {
    }

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

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }
}
