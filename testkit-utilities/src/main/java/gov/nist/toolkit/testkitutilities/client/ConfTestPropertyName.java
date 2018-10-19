package gov.nist.toolkit.testkitutilities.client;


/**
 * These properties are used to set/override Conformance Test Tool Runtime properties.
 * These properties are applied to the entire Test level and its Section levels.
 */
public enum ConfTestPropertyName {
    // Property names
    ENABLE_SECTION_PROPERTIES("enableSectionProperties"),
    // Run entire test at once
    RUN_ENTIRE_TEST("enableRunEntireTest"),
    // Setting externalStart to False will enable the Test Bar Run button.
    EXTERNAL_START("externalStart"),
    // Change the target SiteSpec for this Test to this SiteSpec. It is used for simulator test data loading purposes.
    TARGET_SIM("targetSim"),
    TARGET_SIM_ACTORTYPE_SHORTNAME("targetSimActorTypeShortName");



    String propertyName;

    ConfTestPropertyName() {
    }

    ConfTestPropertyName(String name) {
        propertyName = name;
    }

    static public ConfTestPropertyName find(String s) {
        if (s == null || "".equals(s)) throw new IllegalArgumentException("Invalid input");
        for (ConfTestPropertyName n : values()) {
            if (n.propertyName.equals(s)) return n;
            try {
                if (n == ConfTestPropertyName.valueOf(s)) return n;
            } catch (IllegalArgumentException e) {
                // continue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return propertyName;
    }


}
