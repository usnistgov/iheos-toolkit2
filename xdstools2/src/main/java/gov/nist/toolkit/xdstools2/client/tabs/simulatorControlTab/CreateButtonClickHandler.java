package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.TestSessionState;

class CreateButtonClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	TestSessionState testSessionState;
	
	CreateButtonClickHandler(SimulatorControlTab simulatorControlTab, TestSessionState testSessionState) {
		this.simulatorControlTab = simulatorControlTab;
		this.testSessionState = testSessionState;
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
		SimId simId = new SimId(testSessionState.getTestSessionName(), rawSimId);
		simulatorControlTab.getNewSimulator(actorTypeName, simId);
	}
	
}
