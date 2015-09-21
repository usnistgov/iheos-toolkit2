package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;

class CreateButtonClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	TestSessionManager2 testSessionManager;
	
	CreateButtonClickHandler(SimulatorControlTab simulatorControlTab, TestSessionManager2 testSessionManager) {
		this.simulatorControlTab = simulatorControlTab;
		this.testSessionManager = testSessionManager;
	}

	public void onClick(ClickEvent event) {
		int actorTypeIndex = simulatorControlTab.actorSelectListBox.getSelectedIndex();
		String actorTypeName = simulatorControlTab.actorSelectListBox.getItemText(actorTypeIndex);
		if (actorTypeName == null || actorTypeName.equals("")) {
			new PopupMessage("Select actor type first");
			return;
		}
		String rawSimId = simulatorControlTab.newSimIdTextBox.getValue();
		if (rawSimId == null || rawSimId.equals("")) {
			new PopupMessage("Enter Simulator ID");
			return;
		}
		if (!testSessionManager.isTestSessionValid()) {
			new PopupMessage("Must Select a Test Session");
			return;
		}
		SimId simId = new SimId(testSessionManager.getCurrentTestSession(), rawSimId);
		simulatorControlTab.createNewSimulator(actorTypeName, simId);
	}
	
}
