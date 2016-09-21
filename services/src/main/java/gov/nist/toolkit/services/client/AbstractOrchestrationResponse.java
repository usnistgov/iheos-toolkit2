package gov.nist.toolkit.services.client;

import gov.nist.toolkit.results.client.TestInstance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
abstract public class AbstractOrchestrationResponse extends RawResponse {
    private List<TestInstance> orchestrationTests = new ArrayList<>();  // test definitions used to build the orchestration
    private  String message = "";

    public List<TestInstance> getOrchestrationTests() {
        return orchestrationTests;
    }

    public void setOrchestrationTests(List<TestInstance> orchestrationTests) {
        this.orchestrationTests = orchestrationTests;
    }

    public void addOrchestrationTest(TestInstance testInstance) {
        this.orchestrationTests.add(testInstance);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
