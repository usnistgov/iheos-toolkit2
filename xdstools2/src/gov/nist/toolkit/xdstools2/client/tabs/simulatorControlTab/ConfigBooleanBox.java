package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;

public class ConfigBooleanBox {
	SimulatorConfigElement ele;

	public ConfigBooleanBox(SimulatorConfigElement ele, FlexTable tbl, int row) {
		this.ele = ele;

		tbl.setText(row, 0, ele.name.replace('_', ' '));
		
		CheckBox cb = new CheckBox();
		cb.setValue(ele.asBoolean());
		cb.setEnabled(ele.isEditable());
		tbl.setWidget(row, 1, cb);
				
		cb.addClickHandler(new MyClickHandler(ele, cb));

	}

	class MyClickHandler implements ClickHandler {
		SimulatorConfigElement ele;
		CheckBox tb;

		MyClickHandler(SimulatorConfigElement ele, CheckBox tb) {
			this.ele = ele;
			this.tb = tb;
		}

		@Override
		public void onClick(ClickEvent event) {
			ele.setValue(tb.getValue());
		}
	}

}
