package gov.nist.toolkit.testkitutilities;



/**
 *
 */
public class TestCollectionId {
    private TestCollectionType testCollectionType;
    private String testCollectionName;

    public TestCollectionId(TestCollectionType testCollectionType, String testCollectionName) {
        this.testCollectionType = testCollectionType;
        this.testCollectionName = testCollectionName;
    }

    public TestCollectionType getTestCollectionType() {
        return testCollectionType;
    }

    public void setTestCollectionType(TestCollectionType testCollectionType) {
        this.testCollectionType = testCollectionType;
    }

    public String getTestCollectionName() {
        return testCollectionName;
    }

    public void setTestCollectionName(String testCollectionName) {
        this.testCollectionName = testCollectionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCollectionId that = (TestCollectionId) o;

        if (testCollectionType != that.testCollectionType) return false;
        return testCollectionName != null ? testCollectionName.equals(that.testCollectionName) : that.testCollectionName == null;
    }

    @Override
    public int hashCode() {
        int result = testCollectionType != null ? testCollectionType.hashCode() : 0;
        result = 31 * result + (testCollectionName != null ? testCollectionName.hashCode() : 0);
        return result;
    }
}
