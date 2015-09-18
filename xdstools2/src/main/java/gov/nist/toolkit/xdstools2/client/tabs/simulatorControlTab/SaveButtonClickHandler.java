package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

class SaveButtonClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	SimulatorConfig config;
	String testSession;
	
	SaveButtonClickHandler(SimulatorControlTab simulatorControlTab, SimulatorConfig config, String testSession) {
		this.simulatorControlTab = simulatorControlTab;
		this.config = config;
		this.testSession = testSession;
	}

	public void onClick(ClickEvent event) {
		config.updateDocTypeSelection();
		simulatorControlTab.toolkitService.putSimConfig(config, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("saveSimConfig:" + caught.getMessage());
			}

			public void onSuccess(String result) {
				// reload simulators to get updates
				new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);
				
			}
			
		});
	}
	
}
