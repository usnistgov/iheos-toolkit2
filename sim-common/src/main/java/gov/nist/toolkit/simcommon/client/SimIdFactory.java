package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.*;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.io.*;

public class SimIdFactory implements Serializable, IsSerializable {
    public SimIdFactory() {
    }

    static public SimId simIdBuilder(String rawId) {
        String[] parts = rawId.split("__");
        if (parts.length != 2) throw new ToolkitRuntimeException("Not a valid SimId - " + rawId);
        return new SimId(new TestSession(parts[0]), parts[1]);
    }
}
