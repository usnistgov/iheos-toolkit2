package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

class RemoteSiteLoader {
	final SimulatorConfig config;
	final HorizontalPanel boxes;
	SimulatorControlTab simulatorControlTab;
	
	RemoteSiteLoader(SimulatorControlTab simulatorControlTab, SimulatorConfig configs, HorizontalPanel hpanel) {
		this.simulatorControlTab = simulatorControlTab;
		this.config = configs;
		this.boxes = hpanel;
		
		
		simulatorControlTab.toolkitService.getSiteNamesWithRG(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getSiteNamesWithRG:" + caught.getMessage());
			}

			public void onSuccess(List<String> siteNames) {
				for (String name : siteNames) {
					CheckBox box = new CheckBox(name);
					if (config.getRemoteSiteNames().contains(name)) 
						box.setValue(true);
					else
						box.setValue(false);

					box.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							boolean checked = ((CheckBox) event.getSource()).getValue();
							String name = ((CheckBox) event.getSource()).getText();
							
							if (checked)
								config.getRemoteSiteNames().add(name);
							else
								config.getRemoteSiteNames().remove(name);
							
						}
					});
					
					boxes.add(box);
				}
				
				// if remote site names contains any entries not present in sites,
				// remove them and post warning to user
				List<String> obsolete = new ArrayList<String>();
				for (String remoteName : config.getRemoteSiteNames()) {
					if (!siteNames.contains(remoteName))
						obsolete.add(remoteName);
				}
				
				if (!obsolete.isEmpty()) {
					for (String ob : obsolete)
						config.getRemoteSiteNames().remove(ob);
					new PopupMessage("The following sites are no longer part of the actors configuration " +
							"and have been removed from the simulator configuration: " + obsolete
							);
				}
			}
			
		});
	}
}
