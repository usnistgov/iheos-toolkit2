package gov.nist.toolkit.installation.shared;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class TestSession implements Serializable, IsSerializable {
    private String value;
    public static final TestSession DEFAULT_TEST_SESSION = new TestSession("default");


    public TestSession(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "TestSession " + value;
    }

    public void clean() { value = value.replaceAll("\\.", "_").toLowerCase(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSession that = (TestSession) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
