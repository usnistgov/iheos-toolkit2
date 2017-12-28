package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.xdstools2.client.command.command.GetFhirResultCommand;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.MessageDisplay;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRawLogsRequest;

import java.util.List;

/**
 * List<Message> is a collection of request/response pairs, each pair representing
 * a single step.
 */
public class ResponseLoader {
    public static void load(TestInstance testInstance, final String title, final IContentHolder contentHolder) {
        new GetFhirResultCommand(){
            @Override
            public void onComplete(List<Message> messages) {
                contentHolder.clearLogContent();
                for (Message m : messages) {
                    contentHolder.addContent(new MessageDisplay(m, m.getName()).asSinglePanel(), m.getName());
                }
            }
        }.run(new GetRawLogsRequest(ClientUtils.INSTANCE.getCommandContext(), testInstance));
    }
}
