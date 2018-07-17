package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds user test collections as configured in Toolkit.properties and Testkit in External Cache.
 */
public class UserTestCollection implements Serializable, IsSerializable {

    private final List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs = new ArrayList<>();
    private final Map<TestCollectionCode, List<TestInstance>> tcCode2TestInstancesMap = new HashMap<>();
    private TabConfig tabConfig;

    public UserTestCollection() {
    }

    public List<TestCollectionDefinitionDAO> getTestCollectionDefinitionDAOs() {
        return testCollectionDefinitionDAOs;
    }

    public TabConfig getTabConfig() {
        return tabConfig;
    }

    public void setTabConfig(TabConfig tabConfig) {
        this.tabConfig = tabConfig;
    }

    public Map<TestCollectionCode, List<TestInstance>> getTcCode2TestInstancesMap() {
        return tcCode2TestInstancesMap;
    }
}
