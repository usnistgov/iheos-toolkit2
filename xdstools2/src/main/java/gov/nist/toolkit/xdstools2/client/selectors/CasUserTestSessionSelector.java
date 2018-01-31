package gov.nist.toolkit.xdstools2.client.selectors;

/**
 *
 */
public class CasUserTestSessionSelector extends MultiUserTestSessionSelector {
    public CasUserTestSessionSelector() {
        super(true,false,false);
        build();
        link();
    }

}
