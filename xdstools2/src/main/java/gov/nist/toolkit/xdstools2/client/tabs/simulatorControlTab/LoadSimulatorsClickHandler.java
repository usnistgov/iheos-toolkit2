package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.command.GetSimConfigsCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimConfigsRequest;

import java.util.ArrayList;
import java.util.List;

public class LoadSimulatorsClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	String currentTestSession;

	public LoadSimulatorsClickHandler(SimulatorControlTab simulatorControlTab, String currentTestSession) {
		this.simulatorControlTab = simulatorControlTab;
		this.currentTestSession = currentTestSession;
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
			if (!x.equals("")) {
				SimId si;
				try {
					si = new SimId(new TestSession(currentTestSession), x);
				} catch (Exception e) {
					new PopupMessage(e.getMessage());
					return;
				}
				ids.add(si);
			}
		}

		new GetSimConfigsCommand(){
			@Override
			public void onComplete(List<SimulatorConfig> configs) {
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
		}.run(new GetSimConfigsRequest(ClientUtils.INSTANCE.getCommandContext(),ids));
	}

}
