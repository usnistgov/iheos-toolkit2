package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.io.Serializable;

/**
 *
 */
public class RegOrchestrationRequest implements Serializable, IsSerializable {
    private String userName;
    private String environmentName;
    private SiteSpec registrySut;
    private PifType pifType;
    private boolean useExistingSimulator = true;

    public RegOrchestrationRequest() {
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

    public SiteSpec getRegistrySut() {
        return registrySut;
    }

    public void setRegistrySut(SiteSpec registrySut) {
        this.registrySut = registrySut;
    }

    public PifType getPifType() {
        return pifType;
    }

    public void setPifType(PifType pifType) {
        this.pifType = pifType;
    }

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }
}
