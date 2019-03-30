package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import java.util.Objects;

public class IndexFieldValue {
    private String value;

    public IndexFieldValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexFieldValue value1 = (IndexFieldValue) o;
        return value.equals(value1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
