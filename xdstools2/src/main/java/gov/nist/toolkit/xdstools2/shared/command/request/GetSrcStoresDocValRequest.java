package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetSrcStoresDocValRequest extends CommandContext{
    private SiteSpec siteSpec;
    private String ssid;

    public GetSrcStoresDocValRequest() {}
    public GetSrcStoresDocValRequest(CommandContext commandContext, SiteSpec siteSpec, String ssid) {
        copyFrom(commandContext);
        this.siteSpec=siteSpec;
        this.ssid=ssid;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
