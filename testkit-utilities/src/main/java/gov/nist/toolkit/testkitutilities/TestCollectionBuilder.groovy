package gov.nist.toolkit.testkitutilities

import gov.nist.toolkit.installation.Installation
import org.apache.log4j.Logger

/**
 *
 */
class TestCollectionBuilder {
    static final Logger logger = Logger.getLogger(TestCollectionBuilder.class);

    static TestCollection allTests(String envName, String testSession, TestCollectionId testCollectionId) {
        try {
            TestCollection testCollection = new TestCollection(testCollectionId.getTestCollectionName())
            for (File testkitFile:Installation.instance().testkitFiles(envName,testSession)) {
                try {
                    TestKit tk = new TestKit(testkitFile);
                    Map<String, String> testName = tk.getCollection(testCollectionId.testCollectionType.filename, testCollectionId.testCollectionName);  // name ==> description
                    for (String key : testName.keySet()) {
                        if (!collection.containsKey(key)) {
                            collection.put(key, testName.get(key));
                        }
                    }
                    return collection;  // this is in the wrong place
                } catch (Exception e) {
                    // not a problem until the list is exhausted
                }
            }
        } catch (Exception e) {
            logger.error("getCollection", e);
            throw new Exception(e.getMessage());
        }
        // collection was not found -- oops
        throw new Exception("Collection " + testCollectionId.testCollectionType.name + "/" + testCollectionId.testCollectionName + "  was not found");
    }



    static TestCollection addTestStatus() {

    }

    static Map<TestCollectionId, TestCollection> allTestsAndStatuses(List<TestCollectionId> types) {

    }
}
