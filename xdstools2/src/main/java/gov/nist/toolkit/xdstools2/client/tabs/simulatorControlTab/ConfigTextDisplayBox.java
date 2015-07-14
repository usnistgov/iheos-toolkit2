package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

public class ConfigTextDisplayBox {
	
	public ConfigTextDisplayBox(SimulatorConfigElement ele, FlexTable tbl, int row) {
		
		tbl.setWidget(row, 0, new HTML(ele.name.replace('_', ' ')));
		tbl.setWidget(row, 1, new HTML(ele.asString()));
	}
	
}
