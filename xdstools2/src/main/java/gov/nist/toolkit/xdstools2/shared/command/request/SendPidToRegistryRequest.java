package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class SendPidToRegistryRequest extends CommandContext {
    SiteSpec siteSpec;
    Pid pid;

    public SendPidToRegistryRequest() {
    }

    public SendPidToRegistryRequest(CommandContext commandContext, SiteSpec siteSpec, Pid pid) {
        copyFrom(commandContext);
        this.siteSpec = siteSpec;
        this.pid = pid;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public Pid getPid() {
        return pid;
    }
}
