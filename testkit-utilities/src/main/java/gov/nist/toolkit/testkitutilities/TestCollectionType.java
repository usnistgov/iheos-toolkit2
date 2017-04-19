package gov.nist.toolkit.testkitutilities;

/**
 *
 */
public enum TestCollectionType {
    COLLECTION("collections"),     // generic collection
    ACTOR_COLLECTION("actorcollections");    // test collection for a particular actor

    private String filename;

    TestCollectionType(String name) {
        filename = name;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return filename;
    }
}
