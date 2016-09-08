package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.tabs.TextViewerTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;


public class LoadGazelleConfigs  {
	TabContainer container;
//	ToolkitServiceAsync toolkitService;
	String type;
	
	public LoadGazelleConfigs(/*ToolkitServiceAsync toolkitService, */TabContainer container, String type) {
//		this.toolkitService = toolkitService;
		this.container = container;
		this.type = type;  // System name or ALL
	}

	public void load() {
		ClientUtils.INSTANCE.getToolkitServices().reloadSystemFromGazelle(type, new AsyncCallback<String> () {

			public void onFailure(Throwable caught) {
				launchTextViewer(container, "Gazelle", "reloadSystemFromGazelle(\""+ type + "\") call failed: " + caught.getMessage(), true);
			}

			public void onSuccess(String messages) {
				launchTextViewer(container, "Gazelle", messages, false);
			}
			
		});
	}
	
	void launchTextViewer(TabContainer container, String tabName, String contents, boolean escapeHTML) {
		TextViewerTab ttab = new TextViewerTab(escapeHTML);
		ttab.onTabLoad(true, contents, tabName);
	}
	

	
}
