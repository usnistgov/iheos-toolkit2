package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.installation.shared.TestSession;

import java.io.File;
import java.util.*;

/**
 *
 */
public class TestKitSearchPath {
    String environment;
    TestSession testSession;
    List<TestKit> testkits = new ArrayList<>();

    public TestKitSearchPath(String environment, String testSession) {
        this(environment, new TestSession(testSession));
    }

    public TestKitSearchPath(String environment, TestSession testSession) {
        this.environment = environment;
        this.testSession = testSession;
        List<File> testKitRoots = Installation.instance().testkitFiles(environment, testSession);
        for (File root : testKitRoots) {
            testkits.add(new TestKit(root));
        }
    }

    /**
     * Get test ids.  collestionSetName is either collections or actorcollections and the collectionName
     * references a .tc file contained in there
     * @param collectionSetName
     * @param testCollectionId
     * @return
     * @throws Exception
     */
    public Collection<String> getCollectionMembers(String collectionSetName, TestCollectionCode testCollectionId) throws Exception {
        Set<String> tests = new HashSet<>();
        for (TestKit testkit : testkits) {
            List<String> testsForTestkit = testkit.getCollectionMembers(collectionSetName, testCollectionId);
            tests.addAll(testsForTestkit);
        }
        return tests;
    }

    /**
     * Get all members of the testdata set.  Such sets are named such as testdata-registry and testdata-repository
     * @param testdataSetName
     * @return
     */
    public Collection<String> getTestdataSetListing(String testdataSetName) {
        Set<String> listing = new HashSet<>();
        for (TestKit testkit : testkits) {
            List<String> aListing = testkit.getTestdataSetListing(testdataSetName);
            listing.addAll(aListing);
        }
        return listing;
    }

    public TestDefinition getTestDefinition(String testId) {
        for (TestKit testkit : testkits) {
            try {
                return testkit.getTestDef(testId);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public TestKit getTestKitForTest(String testId) {
        for (TestKit testkit : testkits) {
            try {
                TestDefinition testDefinition = testkit.getTestDef(testId);
                return testkit;
            } catch (Exception e) {

            }
        }
        return null;

    }

    public List<File> getPluginDirs(TestKit.PluginType pluginType) {
        List<File> dirs = new ArrayList<>();

        for (TestKit testKit : getTestkits()) {
            dirs.add(testKit.getPluginDir(pluginType));
        }

        return dirs;
    }

    public List<TestKit> getTestkits() {
        return testkits;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("TestKitSearchPath: environment=" + environment + " testSession=" + testSession).append("\n");
        for (TestKit testkit : testkits) {
            buf.append(testkit.toString()).append("\n");
        }

        return buf.toString();
    }

    /**
     *
     * @return Map shortname (actorCodes) ==> long name for all actorcollections
     */
    public Map<String, String> getActorCollectionsNamesAndDescriptions() {
        Map<String, String> names = new HashMap<>();

        for (TestKit testkit : testkits) {
            try {
                // name => description
                Map<String, String> colls = testkit.getCollectionNames("actorcollections");
                for (String name : colls.keySet()) {
                    names.put(name, colls.get(name));
                }
            } catch (Exception e) {

            }
        }
        return names;
    }
}
