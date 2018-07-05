package gov.nist.toolkit.metadataModel;

/**
 * Created by bill on 2/23/17.
 */
public class OldValueNewValueStatus {
    public StatusValue o;
    public StatusValue n;
    public String id;
    public Ro ro;


    public OldValueNewValueStatus(StatusValue oldValue, StatusValue newValue, String id) {
        o = oldValue;
        n = newValue;
        this.id = id;
    }
}
