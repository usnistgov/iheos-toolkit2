package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

public class ConfigEditBox {
	SimulatorConfigElement ele;

	InlineLabel lblTextBox = new InlineLabel();
	TextBox tb = new TextBox();

	public ConfigEditBox() {
	}

	public ConfigEditBox(SimulatorConfigElement ele, FlexTable tbl, int row) {
		configure(ele, tbl, row);
	}

	public void configure(SimulatorConfigElement ele, FlexTable tbl, int row) {
		this.ele = ele;

		lblTextBox.setText(ele.name.replace('_', ' '));
		tbl.setWidget(row, 0, lblTextBox);

		tb.setWidth("550px");
		tb.setText(ele.asString());
		tbl.setWidget(row, 1, tb);

		tb.addChangeHandler(new MyChangeHandler(ele, tb));
	}

	public void setVisible(boolean flag) {
		lblTextBox.setVisible(flag);
		tb.setVisible(flag);
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
			ele.setStringValue(tb.getText());
		}
	}

	public TextBox getTb() {
		return tb;
	}

	public InlineLabel getLblTextBox() {
		return lblTextBox;
	}
}
