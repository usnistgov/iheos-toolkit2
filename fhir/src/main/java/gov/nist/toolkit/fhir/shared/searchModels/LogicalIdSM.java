package gov.nist.toolkit.fhir.shared.searchModels;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class LogicalIdSM implements Serializable, IsSerializable {
    private String value;

    public LogicalIdSM() {}
    public LogicalIdSM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
