package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

/**
 *
 */
public class UniqueIdFactory {

    static public void assign(Metadata metadata) throws XdsInternalException {
        TestConfig testConfig = new TestConfig();
        testConfig.testmgmt_dir = "foofoodir";  // needs to be non-null
        UniqueIdAllocator uia = UniqueIdAllocator.getInstance(testConfig);
        TestMgmt testMgmt = new TestMgmt(testConfig);
        testMgmt.assignUniqueIds(metadata, null);
    }

}
