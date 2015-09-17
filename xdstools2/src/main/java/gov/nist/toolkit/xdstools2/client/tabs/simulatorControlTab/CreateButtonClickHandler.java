package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

class CreateButtonClickHandler implements ClickHandler {
	SimulatorControlTab simulatorControlTab;
	String testSession;
	
	CreateButtonClickHandler(SimulatorControlTab simulatorControlTab, String testSession) {
		this.simulatorControlTab = simulatorControlTab;
		this.testSession = testSession;
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
		SimId simId = new SimId(testSession, rawSimId);
		simulatorControlTab.getNewSimulator(actorTypeName, simId);
	}
	
}
