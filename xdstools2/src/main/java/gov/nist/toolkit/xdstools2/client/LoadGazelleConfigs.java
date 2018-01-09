package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.xdstools2.client.command.command.ReloadSystemFromGazelleCommand;
import gov.nist.toolkit.xdstools2.client.tabs.TextViewerTab;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ActorConfigTab;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ReloadClickHandler;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.ReloadSystemFromGazelleRequest;


public class LoadGazelleConfigs  {
	String type;
	ActorConfigTab actorConfigTab;

	
	public LoadGazelleConfigs(ActorConfigTab actorConfigTab, String type) {
		this.actorConfigTab = actorConfigTab;
		this.type = type;  // System name or ALL
	}

	public void load() {
		new ReloadSystemFromGazelleCommand(){
			@Override
			public void onFailure(Throwable throwable){
				launchTextViewer("Gazelle", "reloadSystemFromGazelle(\""+ type + "\") call failed: " + throwable.getMessage(), true);
			}
			@Override
			public void onComplete(String result) {
				launchTextViewer("Gazelle Log", result, false);
				if (actorConfigTab != null)
					new ReloadClickHandler(actorConfigTab).onClick(null);
			}
		}.run(new ReloadSystemFromGazelleRequest(ClientUtils.INSTANCE.getCommandContext(),type));
	}
	
	void launchTextViewer(String tabName, String contents, boolean escapeHTML) {
		TextViewerTab ttab = new TextViewerTab(escapeHTML);
		ttab.onTabLoad(true, contents, tabName);
	}
	

	
}
