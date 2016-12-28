package gov.nist.toolkit.xdstools2.client;

import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class ObjectSort<T> {
    public void sort(List<T> data, Comparator<T> comparator) {
        boolean changed = true;
        while (changed)
            changed = sort2(data, comparator);
    }

    private boolean sort2(List<T> data1, Comparator<T> comparator) {
        if (data1 == null) return false;
        boolean changed = false;
        for (int i=0; i<data1.size(); i++) {
            int j = i + 1;
            if (j >= data1.size())
                continue;
            T ref = data1.get(i);
            T comp = data1.get(j);
            if (comparator.compare(comp, ref) < 0) {
                // swap
                data1.set(i, comp);
                data1.set(j, ref);
                changed = true;
            }
        }
        return changed;
    }
}
