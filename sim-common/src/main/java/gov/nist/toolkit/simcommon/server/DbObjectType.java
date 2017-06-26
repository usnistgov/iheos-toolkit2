package gov.nist.toolkit.simcommon.server;

/**
 * Object types for elements in SimDb and ResDb
 */
public enum DbObjectType {
    REGISTRY ("Registry"),
    RESOURCES ("Resources");      // FHIR resources that is

    private final String name;

    DbObjectType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
