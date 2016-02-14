package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class RegistryError {
    String errorCode;
    String errorContext;
    String location;
    ResponseStatusType status;

    public RegistryError() {}

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(String errorContext) {
        this.errorContext = errorContext;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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
