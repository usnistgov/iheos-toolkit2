package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfigElement;

public class ConfigTextDisplayBox {
	
	public ConfigTextDisplayBox(SimulatorConfigElement ele, FlexTable tbl, int row) {
		
		tbl.setWidget(row, 0, new HTML(ele.name.replace('_', ' ')));
		tbl.setWidget(row, 1, new HTML(ele.asString()));
	}
	
}
