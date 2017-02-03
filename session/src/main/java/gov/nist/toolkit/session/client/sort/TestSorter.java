package gov.nist.toolkit.session.client.sort;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;

import java.util.*;

/**
 * Sort tests based on SortableTest interface - on dependencies and alphabetic ordering of testname.
 */
public class TestSorter {
    Map<String, TestOverviewDTO> map = new HashMap<>();
    /**
     * Sort by test dependencies and then alphabetically.
     * @return sorted tests collection
     */
    public List<TestOverviewDTO> sort(List<TestOverviewDTO> tests) {
        List<TestOverviewDTO> result = new ArrayList<>();

        for (TestOverviewDTO test : tests) {
            map.put(test.getName(), test);
        }

        while (!map.isEmpty()) {
            String first = first(map.keySet());
            addDependencies(result, first);
        }
        return result;
    }

    private void addDependencies(List<TestOverviewDTO> result, String item) {
        if (contains(result, item))
            return;
        TestOverviewDTO dto = map.get(item);
        if (dto == null)
            return;
        else
            map.remove(item);
        Collection<String> dependencies = dto.getDependencies();
        for (String dependency : dependencies) {
            addDependencies(result, dependency);
        }
        result.add(dto);
    }

    private boolean contains(List<TestOverviewDTO> tests, String item) {
        for (TestOverviewDTO test : tests) {
            if (test.getName().equals(item))
                return true;
        }
        return false;
    }

    private String first(Set<String> in) {
        String first = "zzzzzzzzzzzzzzzzzzz";

        for (String s : in) {
            if (s.compareToIgnoreCase(first) < 0)
                first = s;
        }

        return first;
    }

}
