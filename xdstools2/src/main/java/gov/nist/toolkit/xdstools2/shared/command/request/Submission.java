package gov.nist.toolkit.xdstools2.shared.command.request;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.Serializable;

public class Submission implements Serializable, IsSerializable {
    String pid;
    TestInstance testInstance;

    public Submission() {}

    public String getPid() {
        return pid;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }
}
