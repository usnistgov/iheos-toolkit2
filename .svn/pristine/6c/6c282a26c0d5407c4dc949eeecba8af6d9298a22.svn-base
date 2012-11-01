package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.registrymetadata.client.Code;
import gov.nist.toolkit.results.client.CodeConfiguration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CodePicker extends DialogBox {
	int listSize = 25;
	final ListBox availableCodes = new ListBox();
	final ListBox chosenCodes = new ListBox();

	@SuppressWarnings("unchecked")
	public CodePicker(CodeConfiguration cc, ListBox toUpdate) throws Exception {
		final CodeConfiguration ccf = cc;
		final ListBox listBoxToUpdate = toUpdate;
		setText("Select Codes from " + cc.name);

		FlexTable mainTable = new FlexTable();

		loadAvailableCodes(cc, false);
		availableCodes.setVisibleItemCount(listSize);
		mainTable.setWidget(0, 0, availableCodes);

		VerticalPanel buttonPanel = new VerticalPanel();
		mainTable.setWidget(0, 1, buttonPanel);

		for (int i=0; i<listBoxToUpdate.getItemCount(); i++) {
			chosenCodes.addItem(listBoxToUpdate.getValue(i));
		}

		chosenCodes.setVisibleItemCount(listSize);
		mainTable.setWidget(0, 2, chosenCodes);


		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				CodePicker.this.hide();

				listBoxToUpdate.clear();
				for (int i=0; i<chosenCodes.getItemCount(); i++) {
					listBoxToUpdate.addItem(chosenCodes.getValue(i));
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				CodePicker.this.hide();
			}

		});

		Button add = new Button("Add==>");
		add.addClickHandler(new AddClickHandler());
		buttonPanel.add(add);

		Button rm = new Button("<==Remove");
		rm.addClickHandler(new RmClickHandler());
		buttonPanel.add(rm);
		
		final CheckBox showDetail = new CheckBox("Detail");
		buttonPanel.add(showDetail);
		
		showDetail.addValueChangeHandler(new ValueChangeHandler() {

			public void onValueChange(ValueChangeEvent event) {
				loadAvailableCodes (ccf, showDetail.getValue());
			}
			
		});

		mainTable.setWidget(1, 1, ok);
		mainTable.setWidget(1, 2, cancel);

		setWidget(mainTable);

	}

	private void loadAvailableCodes(CodeConfiguration cc, boolean showDetail)  {
		availableCodes.clear();
		for (String codeDef : cc.codes) {
			try {
			String value;
			if (showDetail)
				value = codeDef;
			else
				value = new Code(codeDef).display;
			availableCodes.addItem(value, codeDef);
			} catch (Exception e) {}
		}
	}

	boolean chosenHasCode(String codeDef) {
		if (codeDef == null || codeDef.equals(""))
			return false;
		for (int i=0; i<chosenCodes.getItemCount(); i++) {
			if ( codeDef.equals(chosenCodes.getValue(i)))
				return true;
		}
		return false;
	}

	class AddClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			int selectedIndex = availableCodes.getSelectedIndex();
			if ( selectedIndex == -1)
				return;
			String codeDef = availableCodes.getValue(selectedIndex);
			if (!chosenHasCode(codeDef))
				chosenCodes.addItem(codeDef);
		}
	}

	class RmClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			int selectedIndex = chosenCodes.getSelectedIndex();
			if (selectedIndex == -1)
				return;
			chosenCodes.removeItem(selectedIndex);
		}

	}


}
