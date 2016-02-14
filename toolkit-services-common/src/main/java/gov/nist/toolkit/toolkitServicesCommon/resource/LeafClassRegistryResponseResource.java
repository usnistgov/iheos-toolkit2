package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.RegistryError;
import gov.nist.toolkit.toolkitServicesCommon.ResponseStatusType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@XmlRootElement
public class LeafClassRegistryResponseResource implements LeafClassRegistryResponse {
    ResponseStatusType status;
    List<RegistryError> registryErrorList = new ArrayList<RegistryError>();
    List<String> leafClassList = new ArrayList<String>();

    @Override
    public ResponseStatusType getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusType status) {
        this.status = status;
    }

    @Override
    public List<RegistryError> getErrorList() {
        return registryErrorList;
    }

    public void setErrorList(List<RegistryError> errorList) {
        this.registryErrorList = errorList;
    }

    @Override
    public List<String> getLeafClasses() {
        return leafClassList;
    }

    public void setLeafClassList(List<String> leafClassList) {
        this.leafClassList = leafClassList;
    }

    public void addLeafClass(String leafClass) {
        leafClassList.add(leafClass);
    }
}
