package gov.nist.toolkit.installation.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class TestSession implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    private String value;
    public transient static final TestSession DEFAULT_TEST_SESSION = new TestSession("default");
    public transient static final TestSession GAZELLE_TEST_SESSION = new TestSession("gazelle");
    public transient static final TestSession CAT_TEST_SESSION = new TestSession("cat");

    private TestSession() {}

    public TestSession(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
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
