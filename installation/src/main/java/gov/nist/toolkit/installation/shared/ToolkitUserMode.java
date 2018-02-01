package gov.nist.toolkit.installation.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public enum ToolkitUserMode  implements IsSerializable, Serializable {
    SINGLE_USER,
    MULTI_USER,
    CAS_USER;

    ToolkitUserMode() {
    }
}
