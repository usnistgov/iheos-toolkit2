package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class IndexFieldValue implements Comparable<IndexFieldValue> {
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

    @Override
    public int compareTo(IndexFieldValue fieldValue) {
        return this.toString().compareTo(fieldValue.toString());
    }

    public static List<IndexFieldValue> toIndexFieldValues(Collection<String> stringCollection) {
        Iterator<String> it = stringCollection.iterator();
        List<IndexFieldValue> ifvs = new ArrayList<>();
        while (it.hasNext()) {
            ifvs.add(new IndexFieldValue(it.next()));
        }
        return ifvs;
     }
}
