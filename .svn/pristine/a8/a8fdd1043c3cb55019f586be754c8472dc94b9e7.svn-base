package gov.nist.toolkit.xdstools2.client.tabs.directSenderTab;

import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class LoadTestdataList {
	ToolkitServiceAsync toolkit;
	String listName;
	ListBox listbox;
	String firstElement = null;

	public LoadTestdataList(ToolkitServiceAsync toolkit, String listName, ListBox listbox, String firstElement) {
		this.toolkit = toolkit;
		this.listName = listName;
		this.listbox = listbox;
		this.firstElement = firstElement;
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
			listbox.clear();
			if (firstElement != null) {
				listbox.addItem(firstElement);
			}
			for (String i : arg0)
				listbox.addItem(i);
		}

	};
}
