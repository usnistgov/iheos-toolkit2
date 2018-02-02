package gov.nist.toolkit.xdstools2.client.selectors;

import gov.nist.toolkit.xdstools2.client.util.TabWatcher;

/**
 *
 */
public class CasUserTestSessionSelector extends MultiUserTestSessionSelector {
    public CasUserTestSessionSelector(TabWatcher tabWatcher) {
        super(true,false,false,"CAS");
        this.tabWatcher = tabWatcher;
        build();
        link();
    }

    @Override
    protected void setChangeAccess() {
        // Do nothing for CAS here.
    }

}
