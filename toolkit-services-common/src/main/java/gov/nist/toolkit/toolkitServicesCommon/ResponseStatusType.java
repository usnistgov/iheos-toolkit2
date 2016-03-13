package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This includes values used on RegistryResponse messages (SUCCESS, FAILURE, PARTIAL_SUCCESS)
 * and values used on individual RegistryError elements (ERROR, WARNING)
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

    static public ResponseStatusType getStatus(String statusString) throws Exception {
        if (statusString == null)
            throw new Exception("Do not understand RegistryResponse status of " + statusString);
        for (ResponseStatusType t : values()) {
            if (statusString.equals(t.value)) return t;
        }
        throw new Exception("Do not understand RegistryResponse status of " + statusString);
    }
}
