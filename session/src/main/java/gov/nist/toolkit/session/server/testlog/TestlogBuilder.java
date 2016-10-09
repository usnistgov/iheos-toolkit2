package gov.nist.toolkit.session.server.testlog;

import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.SectionLogMapDTO;

/**
 *
 */
public class TestlogBuilder {
    private TestLogDetails details;

    public TestlogBuilder(Session session, TestLogDetails testLogDetails) throws Exception {
        this.details = testLogDetails;
    }

    public SectionLogMapDTO build() {
        return details.getSectionLogMapDTO();
    }
}
