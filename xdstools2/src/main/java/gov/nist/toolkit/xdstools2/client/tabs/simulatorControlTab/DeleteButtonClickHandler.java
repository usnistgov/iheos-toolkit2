package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

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
		ClientUtils.INSTANCE.getToolkitServices().deleteConfig(config, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("deleteConfig:" + caught.getMessage());
			}

			public void onSuccess(String result) {
				simulatorControlTab.loadSimStatus();
			}

		});
	}

	public void delete() {
		ClientUtils.INSTANCE.getToolkitServices().deleteConfig(config, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("deleteConfig:" + caught.getMessage());
			}

			public void onSuccess(String result) {
				simulatorControlTab.loadSimStatus();
				((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireSimulatorsUpdatedEvent();
			}

		});
	}

}
