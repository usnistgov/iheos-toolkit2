package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds user test collections as configured in Toolkit.properties and Testkit in External Cache.
 */
public class UserTestCollection implements Serializable, IsSerializable {

    private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs = new ArrayList<>();
    private TabConfig tabConfig;

    public UserTestCollection() {
    }

    public List<TestCollectionDefinitionDAO> getTestCollectionDefinitionDAOs() {
        return testCollectionDefinitionDAOs;
    }

    public void setTestCollectionDefinitionDAOs(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {
        this.testCollectionDefinitionDAOs = testCollectionDefinitionDAOs;
    }

    public TabConfig getTabConfig() {
        return tabConfig;
    }

    public void setTabConfig(TabConfig tabConfig) {
        this.tabConfig = tabConfig;
    }

}
