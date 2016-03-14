package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class LoadGazelleConfigsClickHandler implements ClickHandler {
	TabContainer container;
	ToolkitServiceAsync toolkitService;
	String type;
	
	public LoadGazelleConfigsClickHandler(ToolkitServiceAsync toolkitService, TabContainer container, String type) {
		this.toolkitService = toolkitService;
		this.container = container;
		this.type = type;  // System name or ALL
	}

	@Override
	public void onClick(ClickEvent event) {
		new LoadGazelleConfigs(toolkitService, container, type).load();
	}

}
