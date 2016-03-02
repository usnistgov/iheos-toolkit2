package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.RegistryError;
import gov.nist.toolkit.toolkitServicesCommon.ResponseStatusType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class RegistryErrorResource implements RegistryError {
    String errorCode;
    String errorContext;
    String location;
    ResponseStatusType status;

    public RegistryErrorResource() {}

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(String errorContext) {
        this.errorContext = errorContext;
    }

    @Override
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public ResponseStatusType getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusType status) {
        this.status = status;
    }

    public String toString() {
        return String.format("RegistryError: status=%s code=%s context=%s location=%s", status, errorCode, errorContext, location);
    }
}
