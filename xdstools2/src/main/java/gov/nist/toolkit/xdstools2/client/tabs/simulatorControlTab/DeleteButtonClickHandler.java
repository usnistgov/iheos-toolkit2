package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteConfigCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallback;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.SimConfigRequest;

import java.util.ArrayList;
import java.util.List;

class DeleteButtonClickHandler implements ClickHandler {
	SimulatorConfig config;
	List<SimulatorConfig> configList;
	SimulatorControlTab simulatorControlTab;

	DeleteButtonClickHandler(SimulatorControlTab simulatorControlTab, SimulatorConfig config) {
		this.config = config;
		this.simulatorControlTab = simulatorControlTab;
	}

	DeleteButtonClickHandler(SimulatorControlTab simulatorControlTab, List<SimulatorConfig> configs) {
		this.configList = configs;
		this.simulatorControlTab = simulatorControlTab;
	}

	public void onClick(ClickEvent event) {
		simulatorControlTab.simConfigSuper.delete(config);
		simulatorControlTab.simConfigSuper.refresh();
		delete(true, null);
	}

	public void delete(final boolean refresh, final SimpleCallback simpleCallback) {
		if (configList==null) {
			configList = new ArrayList<>();
			configList.add(config);
		}
		if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
			new PopupMessage("You don't have permission to delete a Simulator in this Test Session");
			return;
		}
		new DeleteConfigCommand(){
			@Override
			public void onComplete(String result) {
				if (refresh) {
					simulatorControlTab.loadSimStatus();
					((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireSimulatorsUpdatedEvent();
				}

				if (simpleCallback!=null) {
					simpleCallback.run();
				}

			}
		}.run(new SimConfigRequest(ClientUtils.INSTANCE.getCommandContext(),configList));
	}

}
