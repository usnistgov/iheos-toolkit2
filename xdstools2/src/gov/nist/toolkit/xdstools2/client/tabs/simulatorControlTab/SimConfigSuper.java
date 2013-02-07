package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Simulator configuration supervisor - holds multiple SimConfigMgr objects, each one manages
 * the details for a single simulator.
 * @author bill
 *
 */
class SimConfigSuper {
	VerticalPanel panel;
	List<SimConfigMgr> mgrs = new ArrayList<SimConfigMgr>();
	SimulatorControlTab simulatorControlTab;

	SimConfigSuper(SimulatorControlTab simulatorControlTab, VerticalPanel panel) {
		this.simulatorControlTab = simulatorControlTab;
		this.panel = panel;
	}
	
	List<String> getIds() {
		List<String> ids = new ArrayList<String>();
		for (SimConfigMgr mgr : mgrs) {
			ids.add(mgr.config.getId());
		}
		return ids;
	}
	
	void add(SimulatorConfig config) {
		delete(config);
		SimConfigMgr mgr = new SimConfigMgr(simulatorControlTab, panel, config);
		mgr.displayInPanel();
		mgrs.add(mgr);
		
		String txt = simulatorControlTab.simIdsTextArea.getText();
		if (txt == null || txt.equals("")) {
			simulatorControlTab.simIdsTextArea.setText(mgr.config.getId());
		} else {
			txt = txt + ", " + mgr.config.getId();
			simulatorControlTab.simIdsTextArea.setText(txt);
		}
	}
	
	boolean contains(String id) {
		for (SimConfigMgr mgr : mgrs) {
			if (id.equals(mgr.config.getId()))
				return true;
		}
		return false;
	}
	
	/*
	 * Delete existing instance of this simulator
	 */
	void delete(SimulatorConfig config) {
		String targetId = config.getId();
		SimConfigMgr toDelete = null;
		for (SimConfigMgr mgr : mgrs) {
			String id = mgr.config.getId();
			if (id.equals(targetId)) {
				toDelete = mgr;
				break;
			}
		}
		if (toDelete != null)
			mgrs.remove(toDelete);
		
	}
	
	void clear() {
		mgrs.clear();
		panel.clear();
	}
	
	void refresh() {
		panel.clear();

		String txt = "";
		for (SimConfigMgr mgr : mgrs) {
			if (txt.equals(""))
				txt = mgr.config.getId();
			else
				txt = txt + ", " + mgr.config.getId();
			
			mgr.removeFromPanel();
			mgr.displayInPanel();
		}
		simulatorControlTab.simIdsTextArea.setText(txt);
		
		simulatorControlTab.updateSimulatorCookies(txt);
	}
	
	void reloadSimulators() {
		simulatorControlTab.toolkitService.getSimConfigs(getIds(), new AsyncCallback<List<SimulatorConfig>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getSimConfigs:" + caught.getMessage());
			}

			public void onSuccess(List<SimulatorConfig> configs) {
				simulatorControlTab.simIdsTextArea.setText("");
				clear();
				for (SimulatorConfig config : configs)
					add(config);
				
				refresh();
			}

		});
	}

}
