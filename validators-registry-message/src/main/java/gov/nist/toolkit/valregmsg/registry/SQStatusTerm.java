package gov.nist.toolkit.valregmsg.registry;

import java.util.ArrayList;
import java.util.List;

public class SQStatusTerm {
    public enum StatusValues { Approved, Deprecated};

    public List<StatusValues> values = new ArrayList<>();

    public static String[] paramNames = {
            "$XDSAssociationStatus"
    };

    public boolean isApprovedAceptable() {
        return values.contains(StatusValues.Approved);
    }

    public boolean isDeprecatedAceptable() {
        return values.contains(StatusValues.Deprecated);
    }
}
