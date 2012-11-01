package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

class DeleteButtonClickHandler implements ClickHandler {
	SimulatorConfig config;
	SimulatorControlTab simulatorControlTab;
	
	DeleteButtonClickHandler(SimulatorControlTab simulatorControlTab, SimulatorConfig config) {
		this.config = config;
		this.simulatorControlTab = simulatorControlTab;
	}

	public void onClick(ClickEvent event) {
		simulatorControlTab.simConfigSuper.delete(config);
		simulatorControlTab.simConfigSuper.refresh();
		simulatorControlTab.toolkitService.deleteConfig(config, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("deleteConfig:" + caught.getMessage());
			}

			public void onSuccess(String result) {
//				// reload simulators to get any updates
//				new LoadSimulatorsClickHandler().loadSimulators();
				
			}
			
		});
		

	}
	
}
