package gov.nist.toolkit.xdstools2.client.tabs.directStatusTab;

import gov.nist.direct.client.MessageLog;
import gov.nist.direct.logger.UserLog;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.SmtpMessageStatus;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MessageLoader  {
	MessageStatusView view;
	ToolkitServiceAsync toolkitService;
	
	public MessageLoader(ToolkitServiceAsync toolkitService, MessageStatusView view) {
		this.view = view;
		this.toolkitService = toolkitService;
	}
	
	public void run(String username) {
		toolkitService.getDirectOutgoingMsgStatus(username, new AsyncCallback<List<MessageLog>> () {

			@Override
			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			@Override
			public void onSuccess(List<MessageLog> result) {
				view.build(result);
				
				for (MessageLog s : result) {
					view.addRow(s);
				}
			}
			
		}); 
	}

}
