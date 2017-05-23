package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetRawLogsRequest extends CommandContext {
    private TestInstance logId;
    public GetRawLogsRequest(){}
    public GetRawLogsRequest(CommandContext commandContext, TestInstance logId) {
        this.copyFrom(commandContext);
        this.logId=logId;
    }

    public TestInstance getLogId() {
        return logId;
    }

    public void setLogId(TestInstance logId) {
        this.logId = logId;
    }
}
