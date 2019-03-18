package gov.nist.toolkit.xdstools2.client.inspector;

public interface FilterFeature {
    boolean displayFilter(boolean isVisible);
    boolean applyFilter();
    boolean removeFilter();
}
