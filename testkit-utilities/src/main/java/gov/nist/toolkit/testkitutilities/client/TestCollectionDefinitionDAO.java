package gov.nist.toolkit.testkitutilities.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestCollectionCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestCollectionDefinitionDAO implements Serializable, IsSerializable {
    private TestCollectionCode collectionID;
    private String collectionTitle;

    public TestCollectionDefinitionDAO() {}

    public TestCollectionDefinitionDAO(TestCollectionCode collectionID, String collectionTitle) {
        this.collectionID = collectionID;
        this.collectionTitle = collectionTitle;
    }

    public TestCollectionCode getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(TestCollectionCode collectionID) {
        this.collectionID = collectionID;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public boolean isOption() { return collectionID.toString().contains("_"); }

    static public List<TestCollectionDefinitionDAO> getNonOption(List<TestCollectionDefinitionDAO> all) {
        List<TestCollectionDefinitionDAO> out = new ArrayList<>();

        for (TestCollectionDefinitionDAO it : all) {
            if (it.isOption())
                continue;
            out.add(it);
        }

        return out;
    }
}
