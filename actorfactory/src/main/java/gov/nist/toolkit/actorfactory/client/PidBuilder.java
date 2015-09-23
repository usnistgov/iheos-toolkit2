package gov.nist.toolkit.actorfactory.client;

/**
 * Created by bill on 9/21/15.
 */
public class PidBuilder {

    // uses - char to separate pid from name
    // this is only important in UI displays
    public static Pid createPid(String patientId) {
        String[] hyphenParts = patientId.split("-");
        String[] parts = hyphenParts[0].split("\\^");
        if (parts.length < 4)
            return null;   // not valid pid
        String id = parts[0];

        String affinityDomain = parts[3];
        String[] parts2 = affinityDomain.split("&");
        if (parts2.length < 2)
            return null;
        String affinityDomainOid = parts2[1];

        Pid pid = new Pid(affinityDomainOid, id);

        if (hyphenParts.length > 1) {
            pid.setExtra(hyphenParts[1]);
        }

        return pid;
    }
}
