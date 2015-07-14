package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.TextBox;

public class UserConfigEditor {

	boolean hasChanged = false;
	List<SimulatorConfigElement> eles = new ArrayList<SimulatorConfigElement>();
	List<TextBox> boxes = new ArrayList<TextBox>();

	TextBox add(SimulatorConfigElement ele) {
		eles.add(ele);
		TextBox tb = new TextBox();
		boxes.add(tb);
		return tb;
	}

	/**
	 * Update the ActorSimulatorConfigElements from the screen content
	 */
	void update() {
		// this should be obsolete
//		for (int i=0; i<eles.size(); i++) {
//			ActorSimulatorConfigElement ele = eles.get(i);
//			TextBox tb = boxes.get(i);
//			String tbText = tb.getText();
//			if (tbText == null)
//				tbText = "";
//			if (ele.value == null)
//				ele.value = "";
//			if (!ele.value.equals(tbText))
//				hasChanged = true;
//			ele.value = tbText;
//		}
	}


}
