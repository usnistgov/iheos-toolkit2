package gov.nist.toolkit.registrymsg.registry;

import org.apache.axiom.om.OMElement;

import java.util.List;

/**
 *
 */
public class AdhocQueryResponse {
    String status;
    List<RegistryError> registryErrorList;
    OMElement registryErrorListEle;
    OMElement registryObjectListEle;
    OMElement ele;

    public boolean isSuccess() {
        return status != null && status.endsWith(":Success");
    }

    public String getStatus() {
        return status;
    }

    public List<RegistryError> getRegistryErrorList() {
        return registryErrorList;
    }

    public OMElement getRegistryObjectListEle() {
        return registryObjectListEle;
    }

    public OMElement getRegistryErrorListEle() {
        return registryErrorListEle;
    }

    public OMElement getMessage() {
        return ele;
    }
}
