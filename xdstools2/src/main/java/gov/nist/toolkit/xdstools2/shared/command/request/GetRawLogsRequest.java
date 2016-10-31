package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetRawLogsRequest extends CommandContext{
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
