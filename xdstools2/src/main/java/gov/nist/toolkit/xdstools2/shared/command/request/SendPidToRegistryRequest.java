package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 *
 */
public class SendPidToRegistryRequest extends CommandContext {
    SiteSpec siteSpec;
    List<Pid> pid;

    public SendPidToRegistryRequest() {
    }

    public SendPidToRegistryRequest(CommandContext commandContext, SiteSpec siteSpec, List<Pid> pid) {
        copyFrom(commandContext);
        this.siteSpec = siteSpec;
        this.pid = pid;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public List<Pid> getPid() {
        return pid;
    }
}
