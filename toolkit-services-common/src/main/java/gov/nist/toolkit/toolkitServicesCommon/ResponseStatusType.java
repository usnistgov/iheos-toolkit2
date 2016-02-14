package gov.nist.toolkit.toolkitServicesCommon;

import gov.nist.toolkit.xdsexception.XdsException;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public enum ResponseStatusType {
    // for the overall RegistryResponse
    SUCCESS("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"),
    FAILURE("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure"),
    PARTIAL_SUCCESS("urn:ihe:iti:2007:ResponseStatusType:PartialSuccess"),

    // For the individual RegistryError elements
    ERROR(""),
    WARNING("");

    String value;

    ResponseStatusType(String value) {
        this.value = value;
    }

    static public ResponseStatusType getStatus(String statusString) throws XdsException {
        if (statusString == null)
            throw new XdsException("Do not understand RegistryResponse status of " + statusString, "");
        for (ResponseStatusType t : values()) {
            if (statusString.equals(t.value)) return t;
        }
        throw new XdsException("Do not understand RegistryResponse status of " + statusString, "");
    }
}
