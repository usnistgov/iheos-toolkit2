package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;

/**
 *
 */
public class FindDocumentsLauncher implements ClickHandler {
    private Pid pid;
    private SiteSpec siteSpec;
    private boolean onDemand;

    public FindDocumentsLauncher(Pid pid, SiteSpec siteSpec, boolean onDemand) throws Exception {
        this.pid = pid;
        this.siteSpec = siteSpec;
        this.onDemand = onDemand;
        if (siteSpec.getActorType() == null) {
            throw new Exception("FindDocumentsLauncher: SiteSpec must be initialized with ActorType");
        }
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        FindDocumentsTab ftab = new FindDocumentsTab();
        ftab.run(pid, siteSpec, onDemand);
        ftab.onTabLoad(true, "FindDocs");
    }
}
