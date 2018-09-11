package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.registrymetadata.client.Code;
import gov.nist.toolkit.results.client.CodeConfiguration;

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

		Button rm = new Button("<==REMOVE");
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
			addCodeItem(showDetail, codeDef);
		}
	}

	private void addCodeItem(boolean showDetail, String codeDef) {
		try {
        String value;
        if (showDetail)
            value = codeDef;
        else
            value = new Code(codeDef).display;
        availableCodes.addItem(value, codeDef);
        } catch (Exception e) {}
	}

	private boolean listHasCode(final ListBox listBox, String codeDef) {
		if (codeDef == null || codeDef.equals(""))
			return false;
		for (int i=0; i<listBox.getItemCount(); i++) {
			if ( codeDef.equals(listBox.getValue(i)))
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
			if (!listHasCode(chosenCodes, codeDef))
				chosenCodes.addItem(codeDef);
		}
	}

	class RmClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			int selectedIndex = chosenCodes.getSelectedIndex();
			if (selectedIndex == -1)
				return;
			// Metadata Update use case: begin handling of codes that may not exist in the codes configuration
			// Since removing a code that doesn't exist in the codes configuration will not be selectable again, temporarily add it to the code box to be able to restore it again.
			String value = chosenCodes.getValue(selectedIndex);
            if (!listHasCode(availableCodes,value)) {
            	addCodeItem(true, value); // default showDetail to true to avoid mixing it in with the default code configuration
			}
			// end
			chosenCodes.removeItem(selectedIndex);
		}
	}


}
