package gov.nist.toolkit.testengine;

/**
 * Created by onh2 on 1/17/17.
 */
public enum  Sections {
    TESTDATA_XDR("testdata-xds"),
    TESTDATA_REG("testdata-registry"),
    TESTDATA_REPO("testdata-repository"),
    TESTS("tests");

    private final String fieldDescription;

    private Sections(String value) {
        fieldDescription = value;
    }

    public String getSection() {
        return fieldDescription;
    }
}
