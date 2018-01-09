package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class LoadGazelleConfigsClickHandler implements ClickHandler {
	String type;
	
	public LoadGazelleConfigsClickHandler(String type) {
		this.type = type;  // System name or ALL
	}

	@Override
	public void onClick(ClickEvent event) {
		new LoadGazelleConfigs(null, type).load();
	}

}
