package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.TestSessionState;

import java.util.ArrayList;
import java.util.List;

class LoadSimulatorsClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	TestSessionState testSessionState;

	LoadSimulatorsClickHandler(SimulatorControlTab simulatorControlTab, TestSessionState testSessionState) {
		this.simulatorControlTab = simulatorControlTab;
		this.testSessionState = testSessionState;
	}

	public void onClick(ClickEvent event) {
		loadSimulators();
	}

	void loadSimulators() {
		simulatorControlTab.simConfigSuper.panel.clear();
		String idStr = simulatorControlTab.simIdsTextArea.getText();
		String[] parts = idStr.split(",");
		List<SimId> ids = new ArrayList<>();

//		simulatorControlTab.updateSimulatorCookies(idStr);

		for (int i=0; i<parts.length; i++) {
			String x = parts[i].trim();
			if (!x.equals(""))
				ids.add(new SimId(testSessionState.getTestSessionName(), x));
		}

		simulatorControlTab.toolkitService.getSimConfigs(ids, new AsyncCallback<List<SimulatorConfig>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getSimConfigs:" + caught.getMessage());
			}

			public void onSuccess(List<SimulatorConfig> configs) {
				SimConfigSuper s = simulatorControlTab.simConfigSuper;
				simulatorControlTab.simIdsTextArea.setText("");
				s.clear();
				for (SimulatorConfig config : configs) {
					if (config.isExpired()) {
						s.delete(config);
						s.refresh();
					} else {
						s.add(config);
					}
				}
			}

		});
	}

}
