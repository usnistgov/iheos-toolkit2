package gov.nist.toolkit.fhir.shared.searchModels;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class IdentifierSM implements Serializable, IsSerializable {
    private String system;
    private String code;

    public IdentifierSM() {}
    public IdentifierSM(String system, String code) {
        this.system = system;
        this.code = code;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return system + "|" + code;
    }
}
