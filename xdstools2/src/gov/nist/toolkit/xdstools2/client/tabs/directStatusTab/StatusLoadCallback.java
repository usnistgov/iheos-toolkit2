package gov.nist.toolkit.xdstools2.client.tabs.directStatusTab;

import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.SmtpMessageStatus;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class StatusLoadCallback implements AsyncCallback<List<SmtpMessageStatus>> {
	MessageStatusView view;
	
	public StatusLoadCallback(MessageStatusView view) {
		this.view = view;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		new PopupMessage(caught.getMessage());
	}

	@Override
	public void onSuccess(List<SmtpMessageStatus> result) {
		view.build(result);
		
		for (SmtpMessageStatus s : result) {
			view.addRow(s);
		}
	}


}
