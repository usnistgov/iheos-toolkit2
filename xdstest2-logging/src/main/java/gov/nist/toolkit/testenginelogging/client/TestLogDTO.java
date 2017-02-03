package gov.nist.toolkit.testenginelogging.client;

import gov.nist.toolkit.results.client.TestInstance;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class TestLogDTO {
    private TestInstance testInstance;
    // - section name must expand to test/section so that test context is maintained
    // section name => log
    private Map<String, LogFileContentDTO> sectionLogs;
    private List<String> sectionNames;   // this dictates the order of the sections

    public TestLogDTO() {}

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public Map<String, LogFileContentDTO> getSectionLogs() {
        return sectionLogs;
    }

    public void setSectionLogs(Map<String, LogFileContentDTO> sectionLogs) {
        this.sectionLogs = sectionLogs;
    }

    public List<String> getSectionNames() {
        return sectionNames;
    }

    public void setSectionNames(List<String> sectionNames) {
        this.sectionNames = sectionNames;
    }
}
