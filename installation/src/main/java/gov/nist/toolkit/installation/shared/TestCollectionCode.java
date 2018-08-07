package gov.nist.toolkit.installation.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Objects;

/**
 * The encoded value of a test collection.
 * @See ActorOption
 */
public class TestCollectionCode implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    public TestCollectionCode() {
    }

    public TestCollectionCode(String code) {
        setCode(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCollectionCode that = (TestCollectionCode) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return getCode();
    }
}
