package gov.nist.toolkit.fhir.simulators.sim.rep;

public class RepIdUidPair {
    private String repUid;
    private String docUid;

    public RepIdUidPair(String repUid, String docUid) {
        this.repUid = repUid;
        this.docUid = docUid;
    }

    public String getRepUid() {
        return repUid;
    }

    public String getDocUid() {
        return docUid;
    }
}
