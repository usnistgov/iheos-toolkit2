package gov.nist.toolkit.xdstools2.client.tabs.directSenderTab;

import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadTestdataList {
	ToolkitServiceAsync toolkit;
	String listName;
	DirectSenderTab.Display display;

	public LoadTestdataList(ToolkitServiceAsync toolkit, String listName, DirectSenderTab.Display display) {
		this.toolkit = toolkit;
		this.listName = listName;
		this.display = display;
	}

	public void run() {
		toolkit.getTestdataSetListing(listName, loadCallback);
	}

	AsyncCallback<List<String>> loadCallback = new AsyncCallback<List<String>>() {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Error: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(List<String> arg0) {
			display.setMessageSelections(arg0);
	}};
}
