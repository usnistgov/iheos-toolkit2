package gov.nist.toolkit.simcommon.client;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

public class SimIdFactory {
    static public SimId simIdBuilder(String rawId) {
        String[] parts = rawId.split("__");
        if (parts.length != 2) throw new ToolkitRuntimeException("Not a valid SimId - " + rawId);
        return new SimId(new TestSession(parts[0]), parts[1]);
    }
}
