package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

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
