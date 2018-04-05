package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

public class AuthorPicker extends DialogBox {
	int listSize = 25;
	final TextBox addAuthorBox = new TextBox();
	final ListBox authorList = new ListBox();

	@SuppressWarnings("unchecked")
	public AuthorPicker(String title, ListBox toUpdate) throws Exception {
		final ListBox listBoxToUpdate = toUpdate; // addTest the selected author names to the list displayed on the tab, when closing the dialogbox
		setText(title);

		FlexTable mainTable = new FlexTable();
		mainTable.setWidget(0, 0, addAuthorBox);

		VerticalPanel buttonPanel = new VerticalPanel();
		mainTable.setWidget(0, 1, buttonPanel);

		// Add authors entered in a previous edit, to the list of authors
		for (int i=0; i<listBoxToUpdate.getItemCount(); i++) {
			authorList.addItem(listBoxToUpdate.getValue(i));
		}

		authorList.setVisibleItemCount(listSize);
		mainTable.setWidget(0, 2, authorList);


		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AuthorPicker.this.hide();

				listBoxToUpdate.setVisible(true);
				listBoxToUpdate.clear();
				for (int i=0; i<authorList.getItemCount(); i++) {
					listBoxToUpdate.addItem(authorList.getValue(i));
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				AuthorPicker.this.hide();
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
			if (addAuthorBox.getText() == "")
				return;
			authorList.addItem(addAuthorBox.getText());
		}
	}

	class RmClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			int selectedIndex = authorList.getSelectedIndex();
			if (selectedIndex == -1)
				return;
			authorList.removeItem(selectedIndex);
		}

	}


}
