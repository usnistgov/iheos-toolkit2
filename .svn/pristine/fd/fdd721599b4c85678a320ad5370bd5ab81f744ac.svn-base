package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
	 * Manages the content of a single Simulator on the screen
	 * @author bill
	 *
	 */
	class SimConfigMgr {
		/**
		 * 
		 */
		private SimulatorControlTab simulatorControlTab;
		VerticalPanel panel;
		HorizontalPanel hpanel;
		SimulatorConfig config;
		FlexTable tbl = new FlexTable();
		
		SimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config) {
			this.simulatorControlTab = simulatorControlTab;
			this.panel = panel;
			this.config = config;
			
//			tbl.setCellPadding(2);
//			tbl.setCellSpacing(2);
//			tbl.setBorderWidth(1);
		}
		
		void removeFromPanel() {
			if (hpanel != null) {
				panel.remove(hpanel);
				hpanel = null;
			}
		}
		
		void displayInPanel() {
			tbl.clear();			
			int row = 0;
			
			tbl.setWidget(row, 0, simulatorControlTab.html("Simulator Type"));
			tbl.setWidget(row, 1, simulatorControlTab.html(config.getType()));
			
			row++;
			
			tbl.setWidget(row, 0, simulatorControlTab.html("Simulator ID"));
			tbl.setWidget(row, 1, simulatorControlTab.html(config.getId()));
			
			row++;
			
			tbl.setWidget(row, 0, simulatorControlTab.html("Expiration"));
			tbl.setWidget(row, 1, simulatorControlTab.html(config.getExpiration().toString()));
			
			row++;
			
			for (SimulatorConfigElement ele : config.getElements()) {
				if (ele.isString()) {
					if (ele.isEditable()) {
						new ConfigEditBox(ele, tbl, row);
					} else {
						new ConfigTextDisplayBox(ele, tbl, row);
					}
					row++;
				} else if (ele.isBoolean()) {
					new ConfigBooleanBox(ele, tbl, row);
					row++;
				} 
			}
						
			if (config.areRemoteSitesNecessary()) {
				tbl.setWidget(row, 0, this.simulatorControlTab.html(config.getRemoteSitesLabel()));
				HorizontalPanel boxes = new HorizontalPanel();
				tbl.setWidget(row, 1, boxes);
				
				new RemoteSiteLoader(simulatorControlTab, config, boxes);
				
			}
			
			hpanel = new HorizontalPanel();
			
			panel.add(hpanel);
			hpanel.add(tbl);
			
			Button saveButton = new Button("Save");
			saveButton.addClickHandler(new SaveButtonClickHandler(simulatorControlTab, config));
			hpanel.add(saveButton);

			hpanel.add(this.simulatorControlTab.html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
			
			
			Button deleteButton = new Button("Delete");
			deleteButton.addClickHandler(new DeleteButtonClickHandler(simulatorControlTab, config));
			hpanel.add(deleteButton);
			
			panel.add(this.simulatorControlTab.html("<br />"));
		}
		
		
				
		
		
		
	}