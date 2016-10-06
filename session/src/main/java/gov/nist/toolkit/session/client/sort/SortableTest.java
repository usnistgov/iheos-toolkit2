package gov.nist.toolkit.session.client.sort;

import java.util.Collection;

/**
 *
 */
public interface SortableTest {
    public String getName();  // my name
    public Collection<String> getDependencies();  // names of tests I depend on
}
