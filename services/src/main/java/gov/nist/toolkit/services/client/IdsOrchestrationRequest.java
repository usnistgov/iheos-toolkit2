package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 */
public class IdsOrchestrationRequest implements Serializable, IsSerializable {
    String userName;
    String environmentName;
    SiteSpec idsSut;
    private boolean useExistingSimulator = true;
    

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

   /**
    * @return the {@link #idsSut} value.
    */
   public SiteSpec getIdsSut() {
      return idsSut;
   }

   /**
    * @param idsSut the {@link #idsSut} to set
    */
   public void setIdsSut(SiteSpec idsSut) {
      this.idsSut = idsSut;
   }

   /**
    * @return the {@link #useExistingSimulator} value.
    */
   public boolean isUseExistingSimulator() {
      return useExistingSimulator;
   }

   /**
    * @param useExistingSimulator the {@link #useExistingSimulator} to set
    */
   public void setUseExistingSimulator(boolean useExistingSimulator) {
      this.useExistingSimulator = useExistingSimulator;
   }

}
