package gov.nist.toolkit.actorfactory.client;

/**
 * Created by bill on 9/21/15.
 */
public class PidBuilder {

    public static Pid createPid(String patientId) {
        String[] parts = patientId.split("\\^");
        if (parts.length != 4)
            return null;   // not valid pid
        String id = parts[0];
        String affinityDomain = parts[3];
        String[] parts2 = affinityDomain.split("&");
        if (parts2.length < 2)
            return null;
        String affinityDomainOid = parts2[1];

        return new Pid(affinityDomainOid, id);
    }
}
