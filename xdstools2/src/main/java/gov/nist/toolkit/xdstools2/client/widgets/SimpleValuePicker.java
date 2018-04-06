package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleValuePicker extends DialogBox {
	int listSize = 25;
	final TextBox addValueBox = new TextBox();
	final ListBox listBox = new ListBox();

	@SuppressWarnings("unchecked")
	public SimpleValuePicker(String title, ListBox toUpdate) throws Exception {
		final ListBox listBoxToUpdate = toUpdate; // addTest the selected author names to the list displayed on the tab, when closing the dialogbox
		setText(title);

		FlexTable mainTable = new FlexTable();
		mainTable.setWidget(0, 0, addValueBox);

		VerticalPanel buttonPanel = new VerticalPanel();
		mainTable.setWidget(0, 1, buttonPanel);

		// Add authors entered in a previous edit, to the list of authors
		for (int i=0; i<listBoxToUpdate.getItemCount(); i++) {
			listBox.addItem(listBoxToUpdate.getValue(i));
		}

		listBox.setVisibleItemCount(listSize);
		mainTable.setWidget(0, 2, listBox);


		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				SimpleValuePicker.this.hide();

				listBoxToUpdate.setVisible(true);
				listBoxToUpdate.clear();
				for (int i = 0; i< listBox.getItemCount(); i++) {
					listBoxToUpdate.addItem(listBox.getValue(i));
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				SimpleValuePicker.this.hide();
			}

		});

		Button add = new Button("Add==>");
		add.addClickHandler(new AddClickHandler());
		buttonPanel.add(add);

		Button rm = new Button("Remove");
		rm.addClickHandler(new RmClickHandler());
		buttonPanel.add(rm);

		mainTable.setWidget(1, 1, ok);
		mainTable.setWidget(1, 2, cancel);

		setWidget(mainTable);
	}

	class AddClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			if (addValueBox.getText() == "")
				return;
			listBox.addItem(addValueBox.getText());
		}
	}

	class RmClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			int selectedIndex = listBox.getSelectedIndex();
			if (selectedIndex == -1)
				return;
			listBox.removeItem(selectedIndex);
		}

	}

	public static List<String> getValuesFromListBox(ListBox listBox) {
		List<String> values = new ArrayList<String>();

		for (int i=0; i<listBox.getItemCount(); i++) {
			values.add(listBox.getValue(i));
		}

		return values;
	}


}
