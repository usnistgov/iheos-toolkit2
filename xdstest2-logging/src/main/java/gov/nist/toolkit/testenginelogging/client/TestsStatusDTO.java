package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class TestsStatusDTO implements IsSerializable, Serializable {
    private boolean run;
    private boolean passed;
    private List<String> actorCollections;

    public TestsStatusDTO() {
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public List<String> getActorCollections() {
        return actorCollections;
    }

    public void setActorCollections(List<String> actorCollections) {
        this.actorCollections = actorCollections;
    }
}
