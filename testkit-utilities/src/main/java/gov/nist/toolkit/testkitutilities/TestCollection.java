package gov.nist.toolkit.testkitutilities;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TestCollection implements Serializable, IsSerializable {
    private String name;
    private Set<TestInstance> tests = new HashSet<TestInstance>();

    public TestCollection() {
    }

    public TestCollection(String name) {
        this.name = name;
    }

    public void add(TestInstance testInstance) {
        tests.add(testInstance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TestInstance> getTests() {
        return tests;
    }

    public void setTests(Set<TestInstance> tests) {
        this.tests = tests;
    }
}
