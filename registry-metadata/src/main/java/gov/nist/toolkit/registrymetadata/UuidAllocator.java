package gov.nist.toolkit.registrymetadata;

import com.sun.ebxml.registry.util.UUID;
import com.sun.ebxml.registry.util.UUIDFactory;

public class UuidAllocator {
    static UUIDFactory fact = null;

    static public String allocate() {
        if (fact == null)
            fact = UUIDFactory.getInstance();
        UUID uu = fact.newUUID();
        return "urn:uuid:" + uu;
    }

}
