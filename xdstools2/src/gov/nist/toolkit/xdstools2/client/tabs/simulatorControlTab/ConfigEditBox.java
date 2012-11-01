package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

public class ConfigEditBox {
	SimulatorConfigElement ele;

	public ConfigEditBox(SimulatorConfigElement ele, FlexTable tbl, int row) {
		this.ele = ele;

		tbl.setText(row, 0, ele.name.replace('_', ' '));

		TextBox tb = new TextBox();
		tb.setWidth("550px");
		tb.setText(ele.asString());
		tbl.setWidget(row, 1, tb);

		tb.addChangeHandler(new MyChangeHandler(ele, tb));
	}

	class MyChangeHandler implements ChangeHandler {
		SimulatorConfigElement ele;
		TextBox tb;

		MyChangeHandler(SimulatorConfigElement ele, TextBox tb) {
			this.ele = ele;
			this.tb = tb;
		}

		@Override
		public void onChange(ChangeEvent event) {
			ele.setValue(tb.getText());
		}
	}

}
