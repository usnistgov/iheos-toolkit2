package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.Xdstools2;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DirectRegistrationContentValidationTable {
	FlexTable directTable = new FlexTable();

	public VerticalPanel contentValidation() {
		VerticalPanel panel = new VerticalPanel();
		//		FlexTable grid = new FlexTable();

		directTable.setCellSpacing(6);

		panel.add(new HTML("<h3>Content validation</h3>"));

		panel.add( new HTML("All content validation is controlled by the Direct (To) address the content is sent to. " + 
				"<br />The following Direct (To) addresses and the expected content types for validation are ..."));

		int row = 0;
		directTable.setWidget(row, 0, new HTML("<b>Direct (To) address</b>"));
		directTable.setWidget(row, 1, new HTML("<b>Purpose</b>"));

		panel.add(directTable);
		
		TkProps ccdaConfig = tkProps().withPrefixRemoved("direct.reporting.ccdatype");
		for (int i=1; i< 50; i++) {
			String in = Integer.toString(i);
			String type = null;
			String display = null;
			String addr = null;
			try {
				type = ccdaConfig.get("type" + in);
				display = ccdaConfig.get("display" + in);
				addr = ccdaConfig.get("directTo" + in);
			} catch (PropertyNotFoundException e) {
			}
			if (type == null || display == null || addr == null)
				break;
			
			row++;
			directTable.setText(row, 0, addr);
			directTable.setText(row, 1, display);
		}

		return panel;
	}

	public TkProps tkProps() {
		return Xdstools2.tkProps();
	}

}
