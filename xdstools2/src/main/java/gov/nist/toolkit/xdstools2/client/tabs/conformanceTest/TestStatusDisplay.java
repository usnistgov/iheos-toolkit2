package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

/**
 *
 */
public interface TestStatusDisplay {
    void labelSuccess();
    void labelReadme();
    void labelFailure();
    void labelNotRun();
    void addExtraStyle(String name);
    void removeExtraStyle(String name);
}
