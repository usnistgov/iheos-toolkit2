package gov.nist.toolkit.configDatatypes.client;

/**
 *
 */
public class PidBuilder {
    static final String SEPARATOR = ",";

    // uses - char to separate pid from name
    // this is only important in UI displays

    // bill - removed - split - some real PIDs have a - character in them

    public static Pid createPid(String patientId) {
        String[] separatorPart = patientId.split(SEPARATOR);
        String[] parts = separatorPart[0].split("\\^");
//        String[] parts = patientId.split("\\^");
        if (parts.length < 4)
            return null;   // not valid pid
        String id = parts[0];

        String affinityDomain = parts[3];
        String[] parts2 = affinityDomain.split("&");
        if (parts2.length < 2)
            return null;
        String affinityDomainOid = parts2[1];

        Pid pid = new Pid(affinityDomainOid, id);

        if (separatorPart.length > 1) {
            pid.setExtra(separatorPart[1]);
        }

        return pid;
    }
}
